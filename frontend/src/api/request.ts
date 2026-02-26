import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import type { ApiResult } from '@/types'

// 创建 axios 实例
const request: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可以在这里添加 token 等
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<ApiResult<unknown>>) => {
    const res = response.data
    // 如果返回的状态码不是 200，说明接口有错误
    if (res.code !== 200) {
      console.error('API Error:', res.message)
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return response
  },
  (error) => {
    console.error('HTTP Error:', error.message)
    return Promise.reject(error)
  }
)

// 封装请求方法
export function get<T>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<ApiResult<T>>> {
  return request.get<ApiResult<T>>(url, config)
}

export function post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<AxiosResponse<ApiResult<T>>> {
  return request.post<ApiResult<T>>(url, data, config)
}

export function put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<AxiosResponse<ApiResult<T>>> {
  return request.put<ApiResult<T>>(url, data, config)
}

export function del<T>(url: string, config?: AxiosRequestConfig): Promise<AxiosResponse<ApiResult<T>>> {
  return request.delete<ApiResult<T>>(url, config)
}

export default request
