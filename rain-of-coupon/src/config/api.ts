// API配置
const isDevelopment = import.meta.env.DEV

export const API_CONFIG = {
  // 开发环境使用动态主机名，生产环境使用固定域名
  baseURL: isDevelopment 
    ? `http://${window.location.hostname}:8080`
    : 'https://your-production-domain.com',
  
  // 图片资源路径 - 所有图片都在后端的 images/coupon 目录下
  imageURL: isDevelopment 
    ? `http://${window.location.hostname}:8080/image/coupon/`
    : 'https://your-production-domain.com/image/coupon/',
  
  // 优惠券图片路径 - 与主页图片使用相同路径
  couponImageURL: isDevelopment 
    ? `http://${window.location.hostname}:8080/image/coupon/`
    : 'https://your-production-domain.com/image/coupon/'
}