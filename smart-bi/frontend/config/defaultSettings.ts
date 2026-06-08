import { ProLayoutProps } from '@ant-design/pro-components';

const Settings: ProLayoutProps & {
  pwa?: boolean;
  logo?: string;
} = {
  navTheme: 'light',
  colorPrimary: '#2563eb',
  layout: 'mix',
  contentWidth: 'Fluid',
  fixedHeader: true,
  fixSiderbar: true,
  colorWeak: false,
  title: 'YuBI',
  pwa: true,
  logo: 'data:image/svg+xml,' + encodeURIComponent('<svg viewBox="0 0 32 32" fill="none" xmlns="http://www.w3.org/2000/svg"><rect width="32" height="32" rx="8" fill="url(#b)"/><rect x="8" y="14" width="4" height="10" rx="1" fill="white"/><rect x="14" y="10" width="4" height="14" rx="1" fill="white"/><rect x="20" y="12" width="4" height="12" rx="1" fill="white"/><defs><linearGradient id="b" x1="0" y1="0" x2="32" y2="32" gradientUnits="userSpaceOnUse"><stop stop-color="#2563eb"/><stop offset="1" stop-color="#3b82f6"/></linearGradient></defs></svg>'),
  iconfontUrl: '',
};

export default Settings;
