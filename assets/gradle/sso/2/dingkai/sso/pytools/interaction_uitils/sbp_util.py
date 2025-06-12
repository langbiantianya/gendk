import re
from interaction_uitils.shell_wrapper import check_output, check_call


def get_version_by_product(product, log):
    try:
        version = check_output(f"spadmin upgrader version --product {product} --print_to_stdout",
                               print_fun=log.debug).strip()
    except:
        version = get_version_by_product_21(product, log)
    return version


def get_version_by_product_21(product, log):
    version = check_output(f"aradmin version", print_fun=log.debug)
    pattern = rf'\b{product}[\s│]+(\d+.\d+.\d+.\d+)\b'
    return re.search(pattern, version).group(1)


class SBPConfigUtils:
    def __init__(self, log):
        self.log = log
        self.version = Version("1.4.0")

    def set_config(self, key, value):
        if self.version < Version("1.1"):
            com = f'spadmin config set server -p sbp -m web -n {key} -v "{value}"'
        elif Version("1.1") <= self.version < Version("1.1"):
            com = f'spadmin config set product_global -p sbp -n {key} -v "{value}"'
        else:
            com = f'sbpadmin business_config set -p sbp -k {key} -v "{value}"'
        self.log.info(f"command is {com}")
        check_call(com, self.log.debug)

    def get_config(self, key):
        if self.version < Version("1.1"):
            com = f'spadmin config get server -p sbp -m web -n {key}'
        elif Version("1.1") <= self.version < Version("1.1"):
            com = f'spadmin config get product_global -p sbp -n {key}'
        else:
            com = f'sbpadmin business_config get -p sbp -k {key}'
        self.log.info(f"command is {com}")
        return check_output(com, self.log.debug).strip()

    def del_config(self, key):
        if self.version < Version("1.1"):
            com = f'spadmin config delete server -p sbp -m web -n {key}'
        elif Version("1.1") <= self.version < Version("1.1"):
            com = f'spadmin config delete product_global -p sbp -n {key}'
        else:
            # self.log.debug("Please use set to set the default value！")
            return False
        self.log.info(f"command is {com}")
        check_call(com, self.log.debug).strip()
        return True


class Version:
    def __init__(self, version_str):
        self.components = list(map(int, str(version_str).split('.')))

    def __str__(self):
        return '.'.join(str(c) for c in self.components)

    def __eq__(self, other):
        if isinstance(other, Version):
            return self.components == other.components
        return NotImplemented

    def __lt__(self, other):
        if isinstance(other, Version):
            for c1, c2 in zip(self.components, other.components):
                if c1 < c2:
                    return True
                elif c1 > c2:
                    return False
            # If all components are equal, the shorter version is considered smaller
            return len(self.components) < len(other.components)
        return NotImplemented

    def __gt__(self, other):
        if isinstance(other, Version):
            return other < self
        return NotImplemented

    def __le__(self, other):
        if isinstance(other, Version):
            return self == other or self < other
        return NotImplemented

    def __ge__(self, other):
        if isinstance(other, Version):
            return self == other or self > other
        return NotImplemented


if __name__ == '__main__':
    # Example usage
    version1 = Version("2.1.1")
    version2 = Version("1.10.1")

    print(version1 > version2)  # True
    print(version1 < version2)  # False
    print(version1 == version2)  # False
