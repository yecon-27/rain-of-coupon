// 规则管理API接口
import { getToken } from '@/utils/auth'

// 规则数据类型定义
export interface Rule {
  id: number
  ruleType: string
  ruleTitle: string
  ruleContent: string
  ruleOrder: number
  status: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
}

export interface RulesDisplayData {
  activity_rule: Rule[]
  distribution_rule: Rule[]
  usage_rule: Rule[]
}

// 查询参数类型
export interface RulesQuery {
  pageNum?: number
  pageSize?: number
  ruleType?: string
  ruleTitle?: string
  status?: string
}

// API基础URL配置
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

// 获取若依系统的token
const getRuoyiToken = (): string | null => {
  // 从Cookie中获取Admin-Token
  const cookies = document.cookie.split(';')
  for (const cookie of cookies) {
    const [name, value] = cookie.trim().split('=')
    if (name === 'Admin-Token') {
      return value
    }
  }
  return null
}

// 通用请求函数
const apiRequest = async (url: string, options: RequestInit = {}) => {
  // 优先使用若依系统的token
  const ruoyiToken = getRuoyiToken()
  const authToken = getToken()
  const token = ruoyiToken || authToken

  const config: RequestInit = {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` }),
      ...options.headers,
    },
  }

  try {
    const response = await fetch(`${API_BASE_URL}${url}`, config)

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const data = await response.json()
    return data
  } catch (error) {
    console.error('Rules API Request Error:', error)
    throw error
  }
}

// 获取规则列表
export function listRules(query: RulesQuery) {
  const params = new URLSearchParams(query as Record<string, string>).toString()
  return apiRequest(`/redpacket/rules/list?${params}`, {
    method: 'GET',
  })
}

// 获取规则详细信息
export function getRule(id: number) {
  return apiRequest(`/redpacket/rules/${id}`, {
    method: 'GET',
  })
}

// 新增规则
export function addRule(data: Partial<Rule>) {
  return apiRequest('/redpacket/rules', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

// 修改规则
export function updateRule(data: Partial<Rule>) {
  return apiRequest('/redpacket/rules', {
    method: 'PUT',
    body: JSON.stringify(data),
  })
}

// 删除规则
export function delRule(id: number | number[]) {
  return apiRequest(`/redpacket/rules/${id}`, {
    method: 'DELETE',
  })
}

// 根据类型获取规则
export function getRulesByType(ruleType: string) {
  return apiRequest(`/redpacket/rules/type/${ruleType}`, {
    method: 'GET',
  })
}

// 获取前端显示用的规则数据
export function getRulesForDisplay(): Promise<{
  code: number
  data: RulesDisplayData
  msg: string
}> {
  return apiRequest('/redpacket/rules/display', {
    method: 'GET',
  }).catch((error) => {
    console.warn('获取规则数据失败，使用默认数据:', error)
    // 返回默认数据结构，确保前端不会报错
    return {
      code: 200,
      data: {
        activity_rule: [],
        distribution_rule: [
          {
            id: 1,
            ruleType: 'distribution_rule',
            ruleTitle: '发放规则',
            ruleContent: `
            <div class="rule-intro">消费券发放分两个阶段。</div>
            <div class="rule-stage">
              <span class="stage-title">第一阶段</span>发放时间为2025年1月22日早上10:00至25日早上10:00，按计划数发放，发完为止。
            </div>
            <div class="rule-stage">
              <span class="stage-title">第二阶段</span>发放时间为2025年2月6日10:00至18:00，按第一份段未使用的消费券回收数量发放，发完为止。
            </div>
          `,
            ruleOrder: 1,
            status: '0',
            createBy: 'system',
            createTime: new Date().toISOString(),
            updateBy: 'system',
            updateTime: new Date().toISOString(),
            remark: '默认发放规则',
          },
        ],
        usage_rule: [
          {
            id: 2,
            ruleType: 'usage_rule',
            ruleTitle: '使用规则',
            ruleContent: `
            <div class="usage-intro">消费者在符合条件的实体餐饮商家进行消费时，须满足所持消费券对应的使用要求方可核销，每桌限使用一张。</div>
            <div class="usage-detail">消费券使用分两个阶段。</div>
            <div class="rule-stage">
              <span class="stage-title">第一阶段</span>使用时间为2025年1月22日早上10:00至2月4日午夜12:00，期间未使用，消费券失效且被系统回收。
            </div>
            <div class="rule-stage">
              <span class="stage-title">第二阶段</span>使用时间为2025年2月6日早上10:00至2月12日午夜12:00，期间未使用，消费券失效。
            </div>
          `,
            ruleOrder: 2,
            status: '0',
            createBy: 'system',
            createTime: new Date().toISOString(),
            updateBy: 'system',
            updateTime: new Date().toISOString(),
            remark: '默认使用规则',
          },
        ],
      },
      msg: '使用默认规则数据',
    }
  })
}

// 导出规则
export function exportRules(query: RulesQuery) {
  const params = new URLSearchParams(query as Record<string, string>).toString()
  return apiRequest(`/redpacket/rules/export?${params}`, {
    method: 'POST',
  })
}
