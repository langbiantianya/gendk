import { default as Runtime } from '@sef/runtime';
import React from 'react';
import Nav from './nav';
import { global } from '@sc/global';
import { coreCreator } from '@global/core';
import { withRouter } from 'dva/router';
import { connect } from 'dva';
import tiger from '@sc/tiger'
import demoModuleconfig from './demo.config.json';
import demoRoutesconfig from './demo.routes.js';
// import "./style.less"
import 'sensd/dist/sensd.css';

global.init({ coreCreator })

let runtime;
const DemoContainer = withRouter(connect(({ global }) => ({ global }))((props) => {
  const { children } = props;

  // 登录页不依赖 global 业务单元
  if (/^\/login/.test(location.pathname)) {
    return <>{children}</>;
  }

  return (
    <div
      id="container"
      className='root-container'
    >
      <Nav routes={demoRoutesconfig} />
      <div style={{ zIndex: 1, flex: 1 }}>{children}</div>
    </div>
  )
}))

runtime = new Runtime({
  // 应用根 URL
  basename: '/',
  // modules
  modules: demoModuleconfig,
  // 容器
  Container: DemoContainer,
  sensorsdata: {
    useGlobal: false,
    // 配置扩展程序
    extensions: [{
      name: 'handleError',
      config: {}
    }],
    middlewares: [global.effectProxy]
  },
  middlewares: [
    tiger.exception.interceptGlobalExceptionMiddleware
  ],
})

// TRACK.init(trackConfig);
runtime.init();
tiger.exception.init({ runtime });

export default runtime;
