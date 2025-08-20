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
    
    async setPrizeRecord(clickCount: number) {
      try {
        // 调用后端API保存中奖记录到数据库
        const result = await drawLottery({
          clickedCount: clickCount,
          sessionId: `session_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
        });
    
        // 无论API返回什么，都保存中奖记录（因为游戏逻辑已经判定中奖）
        const imageUrl = this.determinePrizeImage(clickCount);
        let amount = 188;
        let prizeName = '188元优惠券';
        
        if (result.data && result.data.isWin === 1) {
          // 如果API也判定中奖，使用API返回的奖品信息
          amount = this.getPrizeAmount(result.data.prizeName || '');
          prizeName = result.data.prizeName || prizeName;
        } else {
          // 如果API判定未中奖，但游戏逻辑判定中奖，使用默认奖品信息
          amount = this.getPrizeAmount(imageUrl);
          if (imageUrl.includes('888')) prizeName = '888元优惠券';
          else if (imageUrl.includes('588')) prizeName = '588元优惠券';
        }
        
        this.prizeRecord = {
          imageUrl,
          amount,
          timestamp: Date.now(),
          participationId: result.data?.id,
          prizeName
        };
        
        // 持久化存储中奖记录
        localStorage.setItem('prizeRecord', JSON.stringify(this.prizeRecord));
        
        return result.data;
      } catch (error) {
        console.error('保存中奖记录失败:', error);
        // 如果API调用失败，仍然保存本地记录作为备份
        const imageUrl = this.determinePrizeImage(clickCount);
        let amount = 188;
        let prizeName = '188元优惠券';
        
        if (imageUrl.includes('888')) {
          amount = 888;
          prizeName = '888元优惠券';
        } else if (imageUrl.includes('588')) {
          amount = 588;
          prizeName = '588元优惠券';
        }
        
        this.prizeRecord = {
          imageUrl,
          amount,
          timestamp: Date.now(),
          prizeName
        };
        
        localStorage.setItem('prizeRecord', JSON.stringify(this.prizeRecord));
      }
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