import UserForm from './Fragments/UserForm';

export default {
  id: 'sdh.pushSetting.Channel', // 扩展点ID 不要改
  UserForm: () => UserForm,
  canBeUseInSegmentPush:() => () => true,
};
