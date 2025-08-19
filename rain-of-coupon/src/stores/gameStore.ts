import { defineStore } from 'pinia';
import { API_CONFIG } from '@/config/api';

interface PrizeRecord {
  imageUrl: string;
  amount: number;
  timestamp: number;
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
    
    setPrizeRecord(clickCount: number) {
      const imageUrl = this.determinePrizeImage(clickCount);
      const amount = imageUrl.includes('888') ? 888 : 
                    imageUrl.includes('588') ? 588 : 188;
      
      this.prizeRecord = {
        imageUrl,
        amount,
        timestamp: Date.now()
      };
      
      // 持久化存储中奖记录
      localStorage.setItem('prizeRecord', JSON.stringify(this.prizeRecord));
    },
    
    loadPrizeRecord() {
      const savedRecord = localStorage.getItem('prizeRecord');
      if (savedRecord) {
        this.prizeRecord = JSON.parse(savedRecord);
      }
    },
  },
});