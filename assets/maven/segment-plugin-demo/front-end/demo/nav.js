import React from 'react';
import { Menu, Popover, Button, Timeline } from 'sensd';
import { connect } from 'dva';
import style from './nav.less';

const langSubItems = [
  {
    name: 'zh-cn',
    cname: '中文-简体',
  },
  {
    name: 'zh-tw',
    cname: '中文-繁體',
  },
  {
    name: 'en-us',
    cname: 'English',
  },
];

class Nav extends React.Component {
  state = {
    height: 54,
  };

  handleChangeLang = language => {
    this.props.dispatch({
      type: 'global/changeLocale',
      locale: language,
    });
  };

  goto = pathname => {
    this.props.dispatch({
      type: 'global/routerReducer',
      location: { pathname },
    });
  };

  renderMenu = () => {
    const { routes } = this.props;
    return (
      <Menu className={style.menu} mode="horizontal">
        {routes.map(({ text, route, sub = [] }) => (
          <Menu.SubMenu
            title={text}
            onTitleClick={() => {
              this.goto(route);
            }}
            key={text}
          >
            {sub.map(subItem => (
              <Menu.Item
                key={subItem.text}
                onClick={() => {
                  this.goto(subItem.route);
                }}
              >
                {subItem.text}
              </Menu.Item>
            ))}
          </Menu.SubMenu>
        ))}
      </Menu>
    );
  };

  render() {
    const {
      runtime: {
        module: { name },
        route: { match, location },
      },
    } = this.props;
    const { height } = this.state;
    return (
      <nav
        className={style.container}
        style={{
          height: `${height}px`
        }}
      >
        {this.renderMenu()}
        <Menu className={style.menu} mode="horizontal">
          <Menu.SubMenu title="Language">
            {langSubItems.map(subItem => (
              <Menu.Item
                key={subItem.name}
                onClick={() => {
                  this.handleChangeLang(subItem.name);
                }}
              >
                <div className={style.subItemDiv}>
                  <span>{subItem.cname}</span>
                </div>
              </Menu.Item>
            ))}
          </Menu.SubMenu>
        </Menu>
        <Popover
          className={style.debug}
          placement="bottomRight"
          title="Debug infos"
          content={
            <Timeline>
              <Timeline.Item>Module in view: {name}</Timeline.Item>
              <Timeline.Item>Route.match: {JSON.stringify(match)}</Timeline.Item>
              <Timeline.Item>Route.location: {JSON.stringify(location)}</Timeline.Item>
            </Timeline>
          }
          trigger="click"
        >
          <Button>Debug</Button>
        </Popover>
      </nav>
    );
  }
}

export default connect(({ runtime, global }) => ({
  runtime,
  global
}))(Nav);
