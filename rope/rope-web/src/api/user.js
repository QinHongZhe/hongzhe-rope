import request from '@/router/axios';
import config from '@/util/config';

// eslint-disable-next-line no-undef

const baseUrl = `${config.getMenuUrl()}`;

export const loginByUsername = (username, password, code, redomStr) => request({
  url: baseUrl + '/login',
  method: 'post',
  data: {
    username,
    password,
    code,
    redomStr
  }
});

export const getUserInfo = () => request({
  url: '/user/info',
  method: 'get'
});

export const RefeshToken = () => request({
  url: '/user/refresh',
  method: 'post'
});

export const getTopMenu = () => request({
  url: '/user/topMenu',
  method: 'get'
});

export const getMenu = (type = 0) => request({
  url: '/user/menu',
  method: 'get',
  data: {
    type: type
  }
});

export const getMenuAll = () => request({
  url: '/user/menu',
  method: 'get',
  data: {
    type: 0
  }
});

export const getTableData = (page) => request({
  url: '/user/getTable',
  method: 'get',
  data: {
    page
  }
});

export const logout = () => request({
  url: '/user/logout',
  method: 'get'
});

export const captchaUrl = () => {
  return baseUrl + '/captcha';
};
