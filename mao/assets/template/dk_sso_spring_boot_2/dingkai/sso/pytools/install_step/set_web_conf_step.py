import os
import sys

sys.path.append(os.path.join(os.path.abspath(os.path.dirname(os.path.abspath(__file__))), '../'))
from interaction_uitils.base import BaseStep
from interaction_uitils.format_utils import get_format_args
from interaction_uitils.sbp_util import SBPConfigUtils


class SetWebConf(BaseStep):
    def __init__(self, log):
        super().__init__("webConf", log)

    def execute(self, args):
        """
        设置 sbp web 配置
        """
        format_args = get_format_args()
        url = f"http://{format_args['local_ip']}:{format_args['port']}/userinfo"
        sbp = SBPConfigUtils(self.log)
        sbp.set_config("login_user_info_api", url)
