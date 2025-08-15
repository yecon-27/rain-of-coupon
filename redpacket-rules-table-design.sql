-- 红包活动规则表设计
CREATE TABLE redpacket_rules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规则ID',
    rule_type VARCHAR(50) NOT NULL COMMENT '规则类型：activity_rule(活动规则), distribution_rule(发放规则), usage_rule(使用规则)',
    rule_title VARCHAR(200) NOT NULL COMMENT '规则标题',
    rule_content TEXT NOT NULL COMMENT '规则内容（支持HTML格式）',
    rule_order INT DEFAULT 0 COMMENT '显示顺序',
    status CHAR(1) DEFAULT '0' COMMENT '状态：0正常，1停用',
    create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注'
) COMMENT = '红包活动规则表';

INSERT INTO redpacket_rules (rule_type, rule_title, rule_content, rule_order) VALUES
('activity_rule', '活动规则', '消费券发放分两个阶段。', 1),
('distribution_rule', '发放规则', '<div class="rule-stage"><span class="stage-title">第一阶段</span>发放时间为2025年1月22日早上10:00至25日早上10:00，按计划数发放，发完为止。</div><div class="rule-stage"><span class="stage-title">第二阶段</span>发放时间为2025年2月6日10:00至18:00，按第一份段未使用的消费券回收数量发放，发完为止。</div>', 2),
('usage_rule', '使用规则', '<div class="usage-intro">消费者在符合条件的实体餐饮商家进行消费时，须满足所持消费券对应的使用要求方可核销，每桌限使用一张。</div><div class="usage-detail">消费券使用分两个阶段。</div><div class="rule-stage"><span class="stage-title">第一阶段</span>使用时间为2025年1月22日早上10:00至2月4日午夜12:00，期间未使用，消费券失效且被系统回收。</div><div class="rule-stage"><span class="stage-title">第二阶段</span>使用时间为2025年2月6日早上10:00至2月12日午夜12:00，期间未使用，消费券失效。</div>', 3);

CREATE INDEX idx_rule_type ON redpacket_rules(rule_type);
CREATE INDEX idx_status ON redpacket_rules(status);
CREATE INDEX idx_rule_order ON redpacket_rules(rule_order);