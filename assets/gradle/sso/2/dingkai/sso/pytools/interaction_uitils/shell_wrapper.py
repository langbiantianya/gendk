#!/bin/env python
# -*- coding: UTF-8 -*-

"""
Copyright (c) 2015 SensorsData, Inc. All Rights Reserved
@author padme(jinsilan@sensorsdata.cn)
@brief

执行shell命令的封装，主要是为了记录stderr/stdout/retcode
所有的方法都有print_fun和timeout两个参数，分别用来指定日志文件和超时
日志文件可以用logger

# new in 1.5
增加ssh_call/ssh_check_call/ssh_check_output

只能使用3
"""
import datetime
import os
import paramiko
import socket
import subprocess
import sys
import tempfile


# 默认打印到stderr
def default_print_fun(x):
    return print(x, file=sys.stderr)


# 不打印
def none_print_fun(x):
    return None


def run_cmd(cmd, print_fun=default_print_fun, timeout=600):
    """
    执行命令 返回{'ret': <ret>, 'stdout': <stdout>, 'stderr': <stderr}
    """
    print_fun('running cmd: [%s]' % cmd)
    p = subprocess.Popen(
        cmd,
        shell=True,
        universal_newlines=True,
        stderr=subprocess.PIPE,
        stdout=subprocess.PIPE)
    try:
        (stdout, stderr) = p.communicate(timeout=timeout)
    except subprocess.TimeoutExpired:
        print_fun('timeout!')
        p.kill()
        (stdout, stderr) = p.communicate()
    ret = p.returncode

    print_fun("======")
    print_fun("cmd:\n%s\n\nret:%d\n\nstdout:\n%s\n\nstderr:\n%s\n\n" % (cmd, ret, stdout, stderr))

    return {'ret': ret, 'stdout': stdout, 'stderr': stderr}


def __assert_ret(ret, cmd, action):
    if ret != 0:
        if action:
            raise Exception('failed to %s! ret=%d' % (action, ret))
        else:
            raise Exception('failed to run[%s]! ret=%d' % (cmd, ret))


def check_output(cmd, print_fun=default_print_fun, timeout=600, action=None):
    """
    执行命令 返回output 如果ret非0抛异常
    """
    result = run_cmd(cmd, print_fun, timeout)
    __assert_ret(result['ret'], cmd, action)
    return result['stdout']


def call(cmd, print_fun=default_print_fun, timeout=600):
    """
    执行命令 返回返回码
    """
    return run_cmd(cmd, print_fun, timeout)['ret']


def check_call(cmd, print_fun=default_print_fun, timeout=600, action=None):
    """
    执行命令，检查是否成功，不成功抛异常
    """
    result = run_cmd(cmd, print_fun, timeout)
    __assert_ret(result['ret'], cmd, action)


class ShellClient:
    """跟SSHClient接口一样 用于本机的操作"""

    def run_cmd(self, cmd, print_fun=default_print_fun, timeout=600, encoding='utf-8'):
        return run_cmd(cmd, print_fun, timeout)

    def start_cmd(self, cmd, print_fun=default_print_fun, encoding='utf-8'):
        return LocalProcess(cmd, print_fun, encoding)

    def copy_from_local(self, local_file, remote_file, print_fun=default_print_fun, timeout=600):
        check_call("cp --preserve=all '%s' '%s'" % (local_file, remote_file), print_fun, timeout)

    def copy_dir_from_local(self, local_dir, remote_dir, tmp_dir='/tmp', print_fun=default_print_fun, timeout=600):
        check_call("cp -r --preserve=all '%s/.' '%s'" % (local_dir, remote_dir), print_fun, timeout)

    def copy_from_remote(self, remote_file, local_file, print_fun=default_print_fun, timeout=600):
        check_call("cp --preserve=all '%s' '%s'" % (remote_file, local_file), print_fun, timeout)

    def copy_dir_from_remote(self, local_dir, remote_dir, tmp_dir='/tmp', print_fun=default_print_fun, timeout=600):
        check_call("cp -r --preserve=all '%s' '%s'" % (remote_dir, local_dir), print_fun, timeout)

    def check_output(self, cmd, print_fun=default_print_fun, timeout=600, action=None):
        return check_output(cmd, print_fun, timeout, action)

    def call(self, cmd, print_fun=default_print_fun, timeout=600):
        return call(cmd, print_fun, timeout)

    def check_call(self, cmd, print_fun=default_print_fun, timeout=600, action=None):
        return check_call(cmd, print_fun, timeout, action)

    def close(self):
        pass


class SSHClient:
    def __init__(self, host, name=None, password=None, encoding='utf8', port=22, timeout=None):
        self.host = host
        self.params = {'hostname': host, 'port': port}
        if name:
            self.params['username'] = name
        if password:
            self.params['password'] = password
        if timeout:
            self.params['timeout'] = timeout
        self.encoding = encoding
        self.client = paramiko.SSHClient()
        self.is_connected = False

    def __repr__(self):
        return 'SSHClient(host=%s)' % self.host

    def check_connect(self):
        """主要保证构造函数不要抛异常"""
        if self.is_connected:
            return
        self.client.load_system_host_keys()
        self.client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        self.client.connect(**self.params)
        self.is_connected = True

    def run_cmd(self, cmd, print_fun=default_print_fun, timeout=600):
        self.check_connect()
        stdin_fd, stdout_fd, stderr_fd = self.client.exec_command(cmd, timeout=timeout)
        # 会阻塞
        ret = stdout_fd.channel.recv_exit_status()  # status is 0
        stdout, stderr = stdout_fd.read().decode(self.encoding), stderr_fd.read().decode(self.encoding)
        print_fun("======")
        print_fun("cmd on %s:\n%s\n\nret:%d\n\nstdout:\n%s\n\nstderr:\n%s\n\n" % (self.host, cmd, ret, stdout, stderr))
        return {'ret': ret, 'stdout': stdout, 'stderr': stderr}

    def start_cmd(self, cmd, print_fun=default_print_fun):
        """异步的 启动进程后返回 返回类型为SSHProcess"""
        self.check_connect()
        return SSHProcess(self.client, cmd, print_fun, self.encoding)

    def __copy_mod_from_local(self, local_file, remote_file, print_fun=default_print_fun):
        mod = check_output("stat -c%%a '%s'" % local_file, print_fun).strip()
        self.check_call("chmod %s '%s'" % (mod, remote_file), print_fun)

    def copy_from_local(self, local_file, remote_file, print_fun=default_print_fun, timeout=600):
        self.check_connect()
        # 1. 拷贝文件
        sftp = paramiko.SFTPClient.from_transport(self.client.get_transport())
        sftp.get_channel().settimeout(timeout)
        sftp.put(local_file, remote_file)
        sftp.close()
        # 2. 拷贝权限
        self.__copy_mod_from_local(local_file, remote_file, print_fun)

    def copy_dir_from_local(self, local_dir, remote_dir, tmp_dir='/tmp', print_fun=default_print_fun, timeout=600):
        """先压缩 再拷贝..."""
        self.check_connect()
        timestamp = datetime.datetime.now().strftime('scp_%Y%m%d')
        # 1. 建立一个临时目录 主要是为了保证目录冲突
        tar_dir = tempfile.TemporaryDirectory(prefix=timestamp, dir=tmp_dir)
        self.check_call('mkdir -p %s' % tar_dir.name, print_fun, timeout)
        if self.call('test -d %s' % remote_dir, print_fun) != 0:
            #raise Exception('already has %s on %s' % (remote_dir, self.host))
            self.check_call('mkdir -p %s' % remote_dir, print_fun)
        # 2. 打包
        tar_file = os.path.join(tar_dir.name, 'data.tgz')
        check_call('cd %s && tar czf %s *' % (local_dir, tar_file), print_fun, timeout)
        # 3. 拷贝
        sftp = paramiko.SFTPClient.from_transport(self.client.get_transport())
        sftp.get_channel().settimeout(timeout)
        sftp.put(tar_file, tar_file)
        sftp.close()
        # 4. 解压
        self.check_call('tar xzf %s -C %s' % (tar_file, remote_dir), print_fun)

    def __copy_mod_from_remote(self, remote_file, local_file, print_fun=default_print_fun):
        mod = self.check_output("stat -c%%a '%s'" % remote_file, print_fun).strip()
        check_call("chmod %s '%s'" % (mod, local_file), print_fun)

    def copy_from_remote(self, remote_file, local_file, print_fun=default_print_fun, timeout=600):
        self.check_connect()
        # 1. 拷贝文件
        sftp = paramiko.SFTPClient.from_transport(self.client.get_transport())
        sftp.get_channel().settimeout(timeout)
        sftp.get(remote_file, local_file)
        sftp.close()
        # 2. 拷贝权限
        self.__copy_mod_from_remote(remote_file, local_file, print_fun)

    def copy_dir_from_remote(self, local_dir, remote_dir, tmp_dir='/tmp', print_fun=default_print_fun, timeout=600):
        """先压缩 再拷贝..."""
        self.check_connect()
        timestamp = datetime.datetime.now().strftime('scp_%Y%m%d')
        if call('test -d %s' % local_dir, print_fun) == 0:
            raise Exception('already has %s on local' % local_dir)
        check_call('mkdir -p %s' % local_dir, print_fun)
        # 1. 建立一个临时目录 主要是为了保证目录冲突
        tar_dir = tempfile.TemporaryDirectory(prefix=timestamp, dir=tmp_dir)
        self.check_call('mkdir -p %s' % tar_dir.name, print_fun, timeout)
        # 2. 打包
        tar_file = os.path.join(tar_dir.name, 'data.tgz')
        self.check_call('cd %s && tar czf %s *' % (local_dir, tar_file), print_fun, timeout)
        # 3. 拷贝
        sftp = paramiko.SFTPClient.from_transport(self.client.get_transport())
        sftp.get_channel().settimeout(timeout)
        sftp.get(tar_file, tar_file)
        sftp.close()
        # 4. 解压
        check_call('tar xzf %s -C %s' % (tar_file, local_dir), print_fun)

    def __assert_ret(self, ret, cmd, action):
        if ret != 0:
            if action:
                raise Exception('failed to %s on %s! ret=%d' % (action, self.host, ret))
            else:
                raise Exception('failed to run[%s] on %s! ret=%d' % (cmd, self.host, ret))

    def check_output(self, cmd, print_fun=default_print_fun, timeout=600, action=None) -> object:
        """

        :rtype: object
        """
        result = self.run_cmd(cmd, print_fun, timeout)
        self.__assert_ret(result['ret'], cmd, action)
        return result['stdout']

    def call(self, cmd, print_fun=default_print_fun, timeout=600):
        return self.run_cmd(cmd, print_fun, timeout)['ret']

    def check_call(self, cmd, print_fun=default_print_fun, timeout=600, action=None):
        result = self.run_cmd(cmd, print_fun, timeout)
        self.__assert_ret(result['ret'], cmd, action)

    def close(self):
        self.client.close()


class SSHProcess:
    def __init__(self, client, cmd, print_fun, encoding):
        """调用后返回"""
        self.client = client
        self.print_fun = print_fun
        self.cmd = cmd
        self.encoding = encoding
        self.stdin_fd, self.stdout_fd, self.stderr_fd = self.client.exec_command(cmd)

    def isRunning(self):  # NOSONAR
        """返回是否运行中"""
        return not self.stdout_fd.channel.exit_status_ready()

    def done(self):
        """返回retcode, stdout, stderr"""
        ret = self.stdout_fd.channel.recv_exit_status()  # status is 0
        stdout = self.stdout_fd.read().decode(self.encoding)
        stderr = self.stderr_fd.read().decode(self.encoding)
        return ret, stdout, stderr


class LocalProcess:
    def __init__(self, cmd, print_fun, encoding):
        self.cmd = cmd
        self.print_fun = print_fun
        self.p = subprocess.Popen(
            cmd,
            shell=True,
            universal_newlines=True,
            stderr=subprocess.PIPE,
            stdout=subprocess.PIPE)

    def isRunning(self):  # NOSONAR
        """
        返回是否运行中
        """
        return self.p.poll() is None

    def done(self):
        """返回retcode, stdout, stderr"""
        ret = self.p.returncode
        stdout = self.p.stdout.read()
        stderr = self.p.stderr.read()
        return ret, stdout, stderr


def ssh_check_output(host, cmd, print_fun=default_print_fun, timeout=600,
                     action=None, name=None, password=None, encode='utf8', port=22):
    client = SSHClient(host, name, password, encode, port)
    try:
        return client.check_output(cmd, print_fun, timeout, action)
    finally:
        client.close()


def ssh_call(host, cmd, print_fun=default_print_fun, timeout=600,
             name=None, password=None, port=22):
    client = SSHClient(host, name, password, port=port)
    try:
        return client.call(cmd, print_fun, timeout)
    finally:
        client.close()


def ssh_check_call(host, cmd, print_fun=default_print_fun, timeout=600, action=None,
                   name=None, password=None, port=22):
    client = SSHClient(host, name, password, port=port)
    try:
        return client.check_call(cmd, print_fun, timeout, action)
    finally:
        client.close()


def ssh_key_gen(cmd, src_ssh, print_fun=default_print_fun, timeout=30):
    """
    调用指令生成密钥
    """
    try:
        src_ssh.check_call(cmd, print_fun, timeout)
    except Exception:
        # 由于openssh版本问题，需要有两种指令方案
        print_fun("exception detected, try again without -m parameter")
        src_ssh.check_call(cmd.replace("-m PEM ", ""), print_fun, timeout)


def build_trust(src_ssh, dst_ssh, print_fun=default_print_fun, timeout=30):
    """
    建立信任关系, 可以从src登录到dst, user和password都一致的
    """
    print_fun('try building trust from %s to %s' % (src_ssh.host, dst_ssh.host))

    # papaer work
    src_ssh_dir = '/home/%s/.ssh' % src_ssh.params['username']
    dst_ssh_dir = '/home/%s/.ssh' % dst_ssh.params['username']
    public_key_file = os.path.join(src_ssh_dir, 'id_rsa.pub')
    private_key_file = os.path.join(src_ssh_dir, 'id_rsa')
    authorized_keys_file = os.path.join(dst_ssh_dir, 'authorized_keys')

    # 1. 检查源机器
    # 1.1 查看是否存在 id_rsa 或 id_dsa ，如果存在，表示此机器存在对外的信任关系。
    cmd = 'test -f %s' % public_key_file
    if 0 != src_ssh.call(cmd, print_fun, timeout):
        # 如果本地密钥文件不存在，则需要为被信任的用户创建一个证书。
        print_fun('no public key in src[%s], try creating one.' % src_ssh.host)
        cmd = "ssh-keygen -t rsa -m PEM -f %s -P ''" % private_key_file
        ssh_key_gen(cmd, src_ssh)

    # 1.2 读取id_rsa.pub的内容
    public_key_content = src_ssh.check_output('cat %s' % public_key_file, print_fun, timeout).strip()
    print_fun('find public key in src[%s]: %s' % (src_ssh.host, public_key_content))

    # 2. 开始对目标机器操作
    # 2.1 检查是否存在.ssh目录
    cmd = 'test -d %s' % dst_ssh_dir
    if 0 != dst_ssh.call(cmd, print_fun, timeout):
        print_fun('no ssh dir in dst[%s], try creating one' % dst_ssh.host)
        cmd = "mkdir {dir} && chmod 700 {dir}".format(dir=dst_ssh_dir)
        dst_ssh.check_call(cmd, print_fun, timeout, 'create ssh dir')

    # 2.2 检查是否存在authorized_keys
    cmd = 'test -f %s' % authorized_keys_file
    if 0 != dst_ssh.call(cmd, print_fun, timeout):
        print_fun('no authorized_keys in dst[%s], try creating one' % dst_ssh.host)
        cmd = "touch {key_file} && chmod 600 {key_file}".format(key_file=authorized_keys_file)
        dst_ssh.check_call(cmd, print_fun, timeout, 'create authorized_keys')

    # 2.3 检查是否已经建立了信任关系
    cmd = 'grep --color=never "%s" %s' % (public_key_content, authorized_keys_file)
    if 0 != dst_ssh.call(cmd, print_fun, timeout):
        print_fun('find public key not in dst authorized_keys file, try appending.')
        cmd = "echo \"%s\" >> %s" % (public_key_content, authorized_keys_file)
        dst_ssh.check_call(cmd, print_fun, timeout, 'append public key')

    # 3. 把目标机器的公钥拷贝到源机器的known hosts
    cmd = "ssh -p %d '%s@%s' -o StrictHostKeyChecking=no 'echo lalala'" \
          % (dst_ssh.params['port'], dst_ssh.params['username'], dst_ssh.host)
    src_ssh.check_call(cmd, print_fun, timeout, "generate known host")

    print_fun('build trust succeed')


def build_local_trust_as_root(user, group, print_fun=default_print_fun, ssh_port=22, timeout=30):
    """
    建立本地本用户的信任关系 从本地以本用户ssh到本用户
    """
    print_fun('try building trust from local to local as %s' % user)

    # papaer work
    src_ssh_dir = '/home/%s/.ssh' % user
    dst_ssh_dir = src_ssh_dir
    public_key_file = os.path.join(src_ssh_dir, 'id_rsa.pub')
    private_key_file = os.path.join(src_ssh_dir, 'id_rsa')
    authorized_keys_file = os.path.join(dst_ssh_dir, 'authorized_keys')

    # 1. 检查源机器
    # 1.1 查看是否存在 id_rsa 或 id_dsa ，如果存在，表示此机器存在对外的信任关系。
    cmd = 'test -f %s' % public_key_file
    if 0 != call(cmd, print_fun, timeout):
        # 如果本地密钥文件不存在，则需要为被信任的用户创建一个证书。
        print_fun('no public key in src, try creating one.')
        cmd = """sudo su - %s  -c 'ssh-keygen -t rsa -m PEM -f %s -P "" ' """ % (user, private_key_file)
        try:
            check_call(cmd, print_fun, timeout)
        except Exception:
            print_fun("exception detected, try again without -m parameter")
            check_call(cmd.replace("-m PEM ", ""), print_fun, timeout)

    # 1.2 读取id_rsa.pub的内容
    with open(public_key_file) as f:
        public_key_content = f.read().strip()
    print_fun('find public key: %s' % public_key_content)

    # 2. 开始对目标机器操作
    # 2.1 检查是否存在.ssh目录
    cmd = 'test -d %s' % dst_ssh_dir
    if 0 != call(cmd, print_fun, timeout):
        print_fun('no ssh dir in dst, try creating one')
        cmd = "mkdir {dir} && chmod 700 {dir}".format(dir=dst_ssh_dir)
        check_call(cmd, print_fun, timeout, 'create ssh dir')

    # 2.2 检查是否存在authorized_keys
    cmd = 'test -f %s' % authorized_keys_file
    if 0 != call(cmd, print_fun, timeout):
        print_fun('no authorized_keys in dst, try creating one')
        cmd = "touch {key_file} && chmod 600 {key_file}".format(key_file=authorized_keys_file)
        check_call(cmd, print_fun, timeout, 'create authorized_keys')

    # 2.3 检查是否已经建立了信任关系
    cmd = 'grep --color=never "%s" %s' % (public_key_content, authorized_keys_file)
    if 0 != call(cmd, print_fun, timeout):
        print_fun('find public key not in dst authorized_keys file, try appending.')
        cmd = "echo \"%s\" >> %s" % (public_key_content, authorized_keys_file)
        check_call(cmd, print_fun, timeout, 'append public key')

    # 3. 统一修改.ssh的目录权限
    cmd = 'chown -R %s:%s %s' % (user, group, src_ssh_dir)
    print_fun('chown .ssh')
    check_call(cmd, print_fun, timeout, 'chown ssh dir')

    # 4. 把目标机器的公钥拷贝到源机器的known hosts
    cmd = """su - %s -c "ssh -p %d '%s@%s' -o StrictHostKeyChecking=no 'echo lalala'" """ \
          % (user, ssh_port, user, socket.getfqdn())
    check_call(cmd, print_fun, timeout, "generate known host")

    print_fun('build trust succeed')


def build_cluster_trust(hosts, user, password, print_fun=default_print_fun, timeout=30, port=22):
    ssh_clients = [SSHClient(host, user, password, port=port) for host in hosts]
    for src_ssh in ssh_clients:
        for dst_ssh in ssh_clients:
            build_trust(src_ssh, dst_ssh, print_fun, timeout)
    [ssh_client.close() for ssh_client in ssh_clients]


def build_trust_as_root(src_ssh, src_user, src_group, dst_ssh, dst_user, dst_group, print_fun=default_print_fun,
                        timeout=30):
    """
    建立信任关系, 可以从src登录到dst, user和password都一致的
    """
    assert (src_ssh.params['username'] == 'root')
    assert (dst_ssh.params['username'] == 'root')
    print_fun('try building trust from %s[%s]@%s to %s[%s]s@%s using root privilage' % (
        src_user, src_group, src_ssh.host, dst_user, dst_group, dst_ssh.host))

    # papaer work
    src_ssh_dir = '/home/%s/.ssh' % src_user
    dst_ssh_dir = '/home/%s/.ssh' % dst_user
    public_key_file = os.path.join(src_ssh_dir, 'id_rsa.pub')
    private_key_file = os.path.join(src_ssh_dir, 'id_rsa')
    authorized_keys_file = os.path.join(dst_ssh_dir, 'authorized_keys')

    # 1. 检查源机器
    # 1.1 查看是否存在 id_rsa 或 id_dsa ，如果存在，表示此机器存在对外的信任关系。
    cmd = 'test -f %s' % public_key_file
    if 0 != src_ssh.call(cmd, print_fun, timeout):
        # 如果本地密钥文件不存在，则需要为被信任的用户创建一个证书。
        print_fun('no public key in src[%s], try creating one.' % src_ssh.host)
        cmd = '''su - %s -c "ssh-keygen -t rsa -m PEM -f %s -P ''" ''' % (src_user, private_key_file)
        ssh_key_gen(cmd, src_ssh)
    # 1.2 读取id_rsa.pub的内容
    public_key_content = src_ssh.check_output('cat %s' % public_key_file, print_fun, timeout).strip()
    print_fun('find public key in src[%s]: %s' % (src_ssh.host, public_key_content))

    # 2. 开始对目标机器操作
    # 2.1 检查是否存在.ssh目录
    cmd = 'test -d %s' % dst_ssh_dir
    if 0 != dst_ssh.call(cmd, print_fun, timeout):
        print_fun('no ssh dir in dst[%s], try creating one' % dst_ssh.host)
        cmd = "mkdir {dir} && chmod 700 {dir} && chown -R {user}:{group} {dir}".format(
            dir=dst_ssh_dir, user=dst_user, group=dst_group)
        dst_ssh.check_call(cmd, print_fun, timeout, 'create ssh dir')
    # 2.2 检查是否存在authorized_keys
    cmd = 'test -f %s' % authorized_keys_file
    if 0 != dst_ssh.call(cmd, print_fun, timeout):
        print_fun('no authorized_keys in dst[%s], try creating one' % dst_ssh.host)
        cmd = "touch {key_file} && chmod 600 {key_file} && chown {user}:{group} {key_file}".format(
            key_file=authorized_keys_file, user=dst_user, group=dst_group)
        dst_ssh.check_call(cmd, print_fun, timeout, 'create authorized_keys')
    # 2.3 检查是否已经建立了信任关系
    cmd = 'grep --color=never "%s" %s' % (public_key_content, authorized_keys_file)
    if 0 != dst_ssh.call(cmd, print_fun, timeout):
        print_fun('find public key not in dst authorized_keys file, try appending.')
        cmd = "echo \"%s\" >> %s" % (public_key_content, authorized_keys_file)
        dst_ssh.check_call(cmd, print_fun, timeout, 'append public key')
    # 3. 把目标机器的公钥拷贝到源机器的known hosts
    cmd = '''su - %s -c "ssh -p %d '%s@%s' -o StrictHostKeyChecking=no 'echo lalala'" ''' \
          % (src_user, dst_ssh.params['port'], dst_user, dst_ssh.host)
    src_ssh.check_call(cmd, print_fun, timeout, "generate known host")
    print_fun('build trust succeed')


def build_cluster_trust_as_root(hosts, user, group, root_password, print_fun=default_print_fun, timeout=30, port=22):
    """
    建立集群信任关系, user和password都一致的
    """
    ssh_clients = [SSHClient(host, 'root', root_password, port=port) for host in hosts]
    # papaer work
    ssh_dir = '/home/%s/.ssh' % user
    public_key_file = os.path.join(ssh_dir, 'id_rsa.pub')
    private_key_file = os.path.join(ssh_dir, 'id_rsa')
    authorized_keys_file = os.path.join(ssh_dir, 'authorized_keys')
    known_host_file = os.path.join(ssh_dir, 'known_hosts')
    ecdsa_key_file = '/etc/ssh/ssh_host_ecdsa_key.pub'
    host_key_file = '/etc/ssh/ssh_host_rsa_key.pub'

    hosts_pubkey = {}
    for ssh in ssh_clients:
        assert (ssh.params['username'] == 'root')
        # 1. 检查机器
        # 1.1 查看是否存在 id_rsa 或 id_dsa ，如果存在，表示此机器存在对外的信任关系
        # 如果不存在，查看.ssh目录是否存在
        cmd = 'test -f %s' % public_key_file
        if 0 != ssh.call(cmd, print_fun, timeout):
            # 查看.ssh目录，若不存在则创建目录
            cmd = 'test -d %s' % ssh_dir
            if 0 != ssh.call(cmd, print_fun, timeout):
                print_fun('no ssh dir in dst[%s], try creating one' % ssh.host)
                cmd = "mkdir {dir} && chmod 700 {dir} && chown -R {user}:{group} {dir}".format(
                    dir=ssh_dir, user=user, group=group)
                ssh.check_call(cmd, print_fun, timeout, 'create ssh dir')
            # 如果本地密钥文件不存在，则需要为被信任的用户创建一个证书。
            print_fun('no public key in src[%s], try creating one.' % ssh.host)
            cmd = '''su - %s -c "ssh-keygen -t rsa -m PEM -f %s -P ''" ''' % (user, private_key_file)
            ssh_key_gen(cmd, ssh)
        # 1.2 读取id_rsa.pub的内容
        public_key_content = ssh.check_output('cat %s' % public_key_file, print_fun, timeout).strip()
        print_fun('find public key in src[%s]: %s' % (ssh.host, public_key_content))
        hosts_pubkey[ssh.host] = public_key_content

    ecdsa_keys = {}
    for ssh in ssh_clients:
        # 2.1 检查是否存在authorized_keys
        cmd = 'test -f %s' % authorized_keys_file
        if 0 != ssh.call(cmd, print_fun, timeout):
            print_fun('no authorized_keys in %s, try creating one' % ssh.host)
            cmd = "touch {key_file} && chmod 600 {key_file} && chown {user}:{group} {key_file}".format(
                key_file=authorized_keys_file, user=user, group=group)
            ssh.check_call(cmd, print_fun, timeout, 'create authorized_keys')
        # 2.2 检查是否已经建立了信任关系
        for host, pubkey in hosts_pubkey.items():
            cmd = 'grep --color=never "%s" %s' % (pubkey, authorized_keys_file)
            if 0 != ssh.call(cmd, print_fun, timeout):
                print_fun('find %s public key not in %s authorized_keys file, try appending.' % (host, ssh.host))
                cmd = "echo \"%s\" >> %s" % (pubkey, authorized_keys_file)
                ssh.check_call(cmd, print_fun, timeout, 'append public key')
        # 2.3 获取ssh_host_ecdsa_key，centos6为ssh_host_rsa_key
        if 0 == ssh.call('test -f %s' % ecdsa_key_file, print_fun, timeout):
            ecdsa_key = ssh.check_output('cat %s' % ecdsa_key_file, print_fun, timeout).strip()
            print_fun('Centos7 using file %s as host key' % ecdsa_key_file)
        elif 0 == ssh.call('test -f %s' % host_key_file, print_fun, timeout):
            ecdsa_key = ssh.check_output('cat %s' % host_key_file, print_fun, timeout).strip()
            print_fun('Centos6 using file %s as host key' % host_key_file)
        else:
            raise Exception('there is neither %s nor %s in system, please check' % (host_key_file, ecdsa_key_file))
        fqdn, hostlist, iplist = socket.gethostbyaddr(ssh.host)
        ecdsa_keys[fqdn] = ecdsa_key
        for h in hostlist:
            ecdsa_keys[h] = ecdsa_key
        for i in iplist:
            ecdsa_keys[i] = ecdsa_key
        # 2.4 检查known_hosts是否存在
        cmd = 'test -f %s' % known_host_file
        if 0 != ssh.call(cmd, print_fun, timeout):
            print_fun('no known_hosts in host[%s], try creating one' % ssh.host)
            cmd = "touch {key_file} && chmod 644 {key_file} && chown {user}:{group} {key_file}".format(
                key_file=known_host_file, user=user, group=group)
            ssh.check_call(cmd, print_fun, timeout, 'create known_hosts')

    for ssh in ssh_clients:
        known_hosts = {}
        # 2.5 检查known_hosts并将ecdsa_key写入
        cmd = 'cat %s' % known_host_file
        content = ssh.check_output(cmd, print_fun, timeout)
        lines = content.strip().split('\n')
        for line in lines:
            if not line:
                continue
            rec = line.split(' ', 1)
            known_hosts[rec[0]] = rec[1]
        for name, ecdsa in ecdsa_keys.items():
            if name not in known_hosts.keys():
                print_fun('find %s not in known_host file, try appending.' % name)
                cmd = "echo \"%s %s\" >> %s" % (name, ecdsa, known_host_file)
                ssh.check_call(cmd, print_fun, timeout, 'append ecdsa key')
            # 检查ecdsa key
            elif name in known_hosts.keys() and ecdsa != known_hosts[name]:
                raise Exception('ecdsa key in %s: %s is different from ecdsa key in %s: %s, please check' % (
                    ssh.host, known_host_file, name, ecdsa_key_file))
    print_fun('build trust succeed')
    [ssh_client.close() for ssh_client in ssh_clients]


if __name__ == '__main__':
    ssh_client = SSHClient(host='106.75.29.16', name='root', password='MhxzKhl2015', timeout=10)
    ssh_client.check_call(r"/sbin/pidof nginx | sed 's/ /\n/g' | wc -l", timeout=10)
    ssh_client.close()
    pass  # NOSONAR
