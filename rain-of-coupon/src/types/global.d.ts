// 全局类型声明

declare module '*.js' {
  const content: any
  export default content
}

// NodeJS 类型声明
declare namespace NodeJS {
  interface Timeout {
    ref(): this
    unref(): this
  }
}

// 用户类型
export interface User {
  id: string
  username: string
  email?: string
  avatar?: string
  isAuthenticated: boolean
}

// 扩展 CSSStyleDeclaration 类型
declare global {
  interface CSSStyleDeclaration {
    webkitTapHighlightColor: string
  }
}