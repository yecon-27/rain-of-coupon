// API配置
const isDevelopment = import.meta.env.DEV

export const API_CONFIG = {
  // 开发环境使用动态主机名，生产环境使用固定域名
  baseURL: isDevelopment 
    ? `http://${window.location.hostname}:8080`
    : 'https://your-production-domain.com',
  
  // 图片资源路径
  imageURL: isDevelopment 
    ? `http://${window.location.hostname}:8080/image/coupon/`
    : 'https://your-production-domain.com/image/coupon/'
}