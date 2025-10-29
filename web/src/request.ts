
import axios from 'axios'

// Set config defaults when creating the instance


const MyAxios = axios.create({
  baseURL: 'https://some-domain.com/api/',
  timeout: 60000,
  withCredentials: true, //发送请去时，可以携带cookie
});


// Add a request interceptor 请求拦截
axios.interceptors.request.use(function (config) {
    // Do something before request is sent
    return config;
  }, function (error) {
    // Do something with request error
    return Promise.reject(error);
  }

);

// Add a response interceptor 响应拦截
axios.interceptors.response.use(function onFulfilled(response) {
  // Any status code that lie within the range of 2xx cause this function to trigger
  // Do something with response data

  const {data} = response;
  // 未登录
  if (data.code === 40100) {

  }
  return response;
}, function onRejected(error) {
  // Any status codes that falls outside the range of 2xx cause this function to trigger
  // Do something with response error
  return Promise.reject(error);
});



export default  MyAxios;
