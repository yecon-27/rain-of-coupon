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
    
    async setPrizeRecord(clickCount: number, apiData: { isWin: boolean; prizeName?: string; id?: number }) {
      // Only set prize if backend confirms a win
      if (!apiData.isWin) {  // 改为boolean判断
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
      console.log('🔍 [GameStore] 开始加载用户中奖记录从数据库');
      
      try {
        console.log('🔍 [GameStore] 调用getUserStatus API...');
        const response = await getUserStatus();
        
        console.log('🔍 [GameStore] getUserStatus API完整响应:', response);
        
        // 修复：正确访问数据结构
        const userStatus = response.data || response;
        
        console.log('🔍 [GameStore] 解析后的userStatus:', userStatus);
        console.log('🔍 [GameStore] hasEverWon:', userStatus.hasEverWon);
        console.log('🔍 [GameStore] winRecords长度:', userStatus.winRecords?.length || 0);
        console.log('🔍 [GameStore] winRecords内容:', userStatus.winRecords);
        console.log('🔍 [GameStore] todayParticipations:', userStatus.todayParticipations);
        
        // 检查是否有中奖记录
        if (userStatus.hasEverWon && userStatus.winRecords && userStatus.winRecords.length > 0) {
          console.log('🏆 [GameStore] 用户有中奖记录，处理最新中奖记录');
          
          // Get the latest winning record
          const latestWin = userStatus.winRecords[0];
          console.log('🏆 [GameStore] 最新中奖记录:', latestWin);
          
          // Determine image URL and amount based on prize name
          let imageUrl = `${API_CONFIG.imageURL}188.png`;
          let amount = 188;
          
          if (latestWin.prizeName?.includes('888')) {
            imageUrl = `${API_CONFIG.imageURL}888.png`;
            amount = 888;
            console.log('🏆 [GameStore] 设置为888元奖品');
          } else if (latestWin.prizeName?.includes('588')) {
            imageUrl = `${API_CONFIG.imageURL}588.png`;
            amount = 588;
            console.log('🏆 [GameStore] 设置为588元奖品');
          } else {
            console.log('🏆 [GameStore] 设置为188元奖品（默认）');
          }
          
          this.prizeRecord = {
            imageUrl,
            amount,
            timestamp: new Date(latestWin.participationTime).getTime(),
            participationId: latestWin.id,
            prizeName: latestWin.prizeName || 'Coupon'
          };
          
          console.log('🏆 [GameStore] 设置prizeRecord:', this.prizeRecord);
          console.log('🏆 [GameStore] hasPrize getter结果:', this.hasPrize);
          
          // Sync with localStorage
          localStorage.setItem('prizeRecord', JSON.stringify(this.prizeRecord));
          console.log('🏆 [GameStore] 已同步到localStorage');
        } else if (userStatus.hasEverWon && userStatus.todayParticipations) {
          // 如果hasEverWon为true但winRecords为空，检查todayParticipations中的中奖记录
          console.log('🔍 [GameStore] winRecords为空，检查todayParticipations中的中奖记录');
          
          const winningParticipation = userStatus.todayParticipations.find((p: any) => p.isWin === 1);
          
          if (winningParticipation) {
            console.log('🏆 [GameStore] 在todayParticipations中找到中奖记录:', winningParticipation);
            
            // 根据点击次数确定奖品
            let imageUrl = `${API_CONFIG.imageURL}188.png`;
            let amount = 188;
            let prizeName = 'Coupon 188';
            
            if (winningParticipation.clickedCount >= 20) {
              imageUrl = `${API_CONFIG.imageURL}888.png`;
              amount = 888;
              prizeName = 'Coupon 888';
            } else if (winningParticipation.clickedCount >= 10) {
              imageUrl = `${API_CONFIG.imageURL}588.png`;
              amount = 588;
              prizeName = 'Coupon 588';
            }
            
            this.prizeRecord = {
              imageUrl,
              amount,
              timestamp: new Date(winningParticipation.participationTime).getTime(),
              participationId: winningParticipation.id,
              prizeName
            };
            
            console.log('🏆 [GameStore] 从todayParticipations设置prizeRecord:', this.prizeRecord);
            
            // Sync with localStorage
            localStorage.setItem('prizeRecord', JSON.stringify(this.prizeRecord));
            console.log('🏆 [GameStore] 已同步到localStorage');
          } else {
            console.log('❌ [GameStore] todayParticipations中也没有找到中奖记录');
            this.prizeRecord = null;
            localStorage.removeItem('prizeRecord');
          }
        } else {
          console.log('❌ [GameStore] 用户无中奖记录，清除本地数据');
          console.log('❌ [GameStore] hasEverWon:', userStatus.hasEverWon);
          console.log('❌ [GameStore] winRecords为空或不存在');
          
          // User has no winning records, clear local data
          this.prizeRecord = null;
          localStorage.removeItem('prizeRecord');
          console.log('❌ [GameStore] 已清除prizeRecord和localStorage');
        }
        
        console.log('✅ [GameStore] loadPrizeRecordFromDB完成');
        console.log('✅ [GameStore] 最终prizeRecord:', this.prizeRecord);
        console.log('✅ [GameStore] 最终hasPrize:', this.hasPrize);
        
      } catch (error) {
        console.error('❌ [GameStore] 加载中奖记录失败:', error);
        console.error('❌ [GameStore] 错误详情:', error.message);
        console.error('❌ [GameStore] 错误堆栈:', error.stack);
        
        // Fallback to localStorage to avoid recursion
        const localRecord = localStorage.getItem('prizeRecord');
        if (localRecord) {
          console.log('🔄 [GameStore] 使用localStorage备用数据:', localRecord);
          this.prizeRecord = JSON.parse(localRecord);
        } else {
          console.log('🔄 [GameStore] localStorage也无数据');
        }
      }
    },
    
    // Update loadPrizeRecord to only call FromDB
    async loadPrizeRecord() {
      await this.loadPrizeRecordFromDB();
    }
  },
});