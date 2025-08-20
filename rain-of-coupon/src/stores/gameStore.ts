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
      return 188; // Default value
    },
    
    async setPrizeRecord(clickCount: number, apiData: { isWin: number; prizeName?: string; id?: number }) {
      // Only set prize if backend confirms a win
      if (apiData.isWin !== 1) {
        return;
      }
    
      const imageUrl = this.determinePrizeImage(clickCount);
      const amount = this.getPrizeAmount(apiData.prizeName || '');
      const prizeName = apiData.prizeName || 'Coupon';
    
      this.prizeRecord = {
        imageUrl,
        amount,
        timestamp: Date.now(),
        participationId: apiData.id,
        prizeName
      };
    
      // Persist to storage
      localStorage.setItem('prizeRecord', JSON.stringify(this.prizeRecord));
    },
    
    // Method to clear localStorage
    clearPrizeRecord() {
      this.prizeRecord = null;
      localStorage.removeItem('prizeRecord');
    },
    
    // Load user's prize record from the database
    async loadPrizeRecordFromDB() {
      try {
        const userStatus = await getUserStatus();
        
        // Check if the user has any winning records
        if (userStatus.hasEverWon && userStatus.winRecords.length > 0) {
          // Get the latest winning record
          const latestWin = userStatus.winRecords[0];
          
          // Determine image URL and amount based on prize name
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
            prizeName: latestWin.prizeName || 'Coupon'
          };
          
          // Sync with localStorage
          localStorage.setItem('prizeRecord', JSON.stringify(this.prizeRecord));
        } else {
          // User has no winning records, clear local data
          this.prizeRecord = null;
          localStorage.removeItem('prizeRecord');
        }
      } catch (error) {
        console.error('Failed to load prize record from DB:', error);
        // Fallback to localStorage to avoid recursion
        const localRecord = localStorage.getItem('prizeRecord');
        if (localRecord) {
          this.prizeRecord = JSON.parse(localRecord);
        }
      }
    },
    
    // Update loadPrizeRecord to only call FromDB
    async loadPrizeRecord() {
      await this.loadPrizeRecordFromDB();
    }
  },
});