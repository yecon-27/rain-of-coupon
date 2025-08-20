import { defineStore } from 'pinia';
import { API_CONFIG } from '@/config/api';
import { drawLottery, getUserStatus } from '@/api/lottery';

interface PrizeRecord {
  imageUrl: string;
  amount: number;
  timestamp: number;
  participationId?: number;
  prizeName?: string;
}

export const useGameStore = defineStore('game', {
  state: () => ({
    clickedPacketCount: 0,
    prizeRecord: null as PrizeRecord | null,
  }),
  
  getters: {
    hasPrize(): boolean {
      return this.prizeRecord !== null;
    },
    
    getPrizeImageUrl(): string | null {
      return this.prizeRecord?.imageUrl || null;
    },
  },
  
  actions: {
    incrementClickedPacketCount() {
      this.clickedPacketCount++;
    },
    
    resetClickedPacketCount() {
      this.clickedPacketCount = 0;
    },
    
    determinePrizeImage(clickCount: number): string {
      if (clickCount >= 20) {
        return `${API_CONFIG.imageURL}888.png`;
      } else if (clickCount >= 10) {
        return `${API_CONFIG.imageURL}588.png`;
      } else {
        return `${API_CONFIG.imageURL}188.png`;
      }
    },

    getPrizeAmount(prizeName: string): number {
      if (prizeName.includes('188')) return 188;
      if (prizeName.includes('588')) return 588;
      if (prizeName.includes('888')) return 888;
      return 188; // 默认值
    },
    
    async setPrizeRecord(clickCount: number, apiData: { isWin: number; prizeName?: string; id?: number }) {
      // 只在后端确认中奖时设置
      if (apiData.isWin !== 1) {
        return;
      }
    
      const imageUrl = this.determinePrizeImage(clickCount);
      const amount = this.getPrizeAmount(apiData.prizeName || '');
      const prizeName = apiData.prizeName || '优惠券';
    
      this.prizeRecord = {
        imageUrl,
        amount,
        timestamp: Date.now(),
        participationId: apiData.id,
        prizeName
      };
    
      // 持久化存储
      localStorage.setItem('prizeRecord', JSON.stringify(this.prizeRecord));
    },
    
    // 添加清除localStorage的方法
    clearPrizeRecord() {
      this.prizeRecord = null;
      localStorage.removeItem('prizeRecord');
    },
    
    // 新增：从数据库加载用户中奖记录
    async loadPrizeRecordFromDB() {
      try {
        const userStatus = await getUserStatus();
        
        // 检查用户是否有中奖记录
        if (userStatus.hasEverWon && userStatus.winRecords.length > 0) {
          // 获取最新的中奖记录
          const latestWin = userStatus.winRecords[0];
          
          // 根据奖品名称确定图片URL和金额
          let imageUrl = `${API_CONFIG.imageURL}188.png`;
          let amount = 188;
          
          if (latestWin.prizeName?.includes('888')) {
            imageUrl = `${API_CONFIG.imageURL}888.png`;
            amount = 888;
          } else if (latestWin.prizeName?.includes('588')) {
            imageUrl = `${API_CONFIG.imageURL}588.png`;
            amount = 588;
          }
          
          this.prizeRecord = {
            imageUrl,
            amount,
            timestamp: new Date(latestWin.participationTime).getTime(),
            participationId: latestWin.id,
            prizeName: latestWin.prizeName || '优惠券'
          };
          
          // 同步到localStorage
          localStorage.setItem('prizeRecord', JSON.stringify(this.prizeRecord));
        } else {
          // 用户没有中奖记录，清除本地数据
          this.prizeRecord = null;
          localStorage.removeItem('prizeRecord');
        }
      } catch (error) {
        console.error('从数据库加载中奖记录失败:', error);
        // 修改：直接从 localStorage 加载，避免递归
        const localRecord = localStorage.getItem('prizeRecord');
        if (localRecord) {
          this.prizeRecord = JSON.parse(localRecord);
        }
      }
    },
    
    // 更新 loadPrizeRecord 只调用 FromDB
    async loadPrizeRecord() {
      await this.loadPrizeRecordFromDB();
    }
  },
});