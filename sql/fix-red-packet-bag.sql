-- 修复红包图片资源记录
-- 如果记录不存在则插入，如果存在则更新

-- 先删除可能存在的重复记录
DELETE FROM redpacket_image_resource WHERE resource_key = 'red_packet_bag';

-- 插入正确的记录（使用正确的路径格式 /image/ 而不是 /images/）
INSERT INTO redpacket_image_resource (resource_name, resource_key, file_name, file_path, usage_scene, description) 
VALUES ('红包图片', 'red_packet_bag', 'luckyBag.png', '/image/coupon/luckyBag.png', '红包雨组件', '红包雨中的红包图片');

-- 验证插入结果
SELECT * FROM redpacket_image_resource WHERE resource_key = 'red_packet_bag';

-- 同时检查其他相关资源是否存在
SELECT resource_key, resource_name, file_path FROM redpacket_image_resource 
WHERE resource_key IN ('red_packet_bag', 'timer_btn', 'packet_count', 'home_bg') 
ORDER BY resource_key;