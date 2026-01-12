export const prerender = true;
// 可选：如果需要SPA模式（客户端路由），添加trailSlash或ssr配置
export const ssr = false; // 仅客户端渲染（SPA）时用
export const trailingSlash = 'always'; // 解决静态部署的路径问题