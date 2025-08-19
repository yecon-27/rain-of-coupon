import { defineStore } from 'pinia';
import { API_CONFIG } from '@/config/api';
import { drawLottery } from '@/api/lottery';

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
    
    async setPrizeRecord(clickCount: number) {
      try {
        // 调用后端API保存中奖记录到数据库
        const result = await drawLottery({
          clickedCount: clickCount,
          sessionId: `session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
        });

        if (result.data && result.data.isWin === 1) {
          // 根据中奖结果设置本地记录
          const imageUrl = this.determinePrizeImage(clickCount);
          const amount = this.getPrizeAmount(result.data.prizeName || '');
          
          this.prizeRecord = {
            imageUrl,
            amount,
            timestamp: Date.now(),
            participationId: result.data.id,
            prizeName: result.data.prizeName
          };
          
          // 持久化存储中奖记录
          localStorage.setItem('prizeRecord', JSON.stringify(this.prizeRecord));
          
          return result.data;
        }
      } catch (error) {
        console.error('保存中奖记录失败:', error);
        // 如果API调用失败，仍然保存本地记录作为备份
        const imageUrl = this.determinePrizeImage(clickCount);
        const amount = this.getPrizeAmount('');
        
        this.prizeRecord = {
          imageUrl,
          amount,
          timestamp: Date.now()
        };
        
        localStorage.setItem('prizeRecord', JSON.stringify(this.prizeRecord));
      }
    },
    
    loadPrizeRecord() {
      const savedRecord = localStorage.getItem('prizeRecord');
      if (savedRecord) {
        this.prizeRecord = JSON.parse(savedRecord);
      }
    },
  },
});