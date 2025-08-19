import { defineStore } from 'pinia';

export const useGameStore = defineStore('game', {
  state: () => ({
    clickedPacketCount: 0,
  }),
  actions: {
    incrementClickedPacketCount() {
      this.clickedPacketCount++;
    },
    resetClickedPacketCount() {
      this.clickedPacketCount = 0;
    },
  },
});