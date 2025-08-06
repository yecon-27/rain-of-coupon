🌐 Language Switch | 语言切换:  [English](./README.md) | [中文](./README.cn.md)

## Red Envelope Rain Mini Program
Developed a mini program using Vue 3 and the Ruoyi Framework. Key features include:

- Lottery system: Up to 3 draws per user per day; ends automatically upon winning.

- Traffic control: Detects high user traffic and displays congestion notifications.

- Interactive animations: Falling red envelopes and clickable reward triggers.

- Coupon package page: Designed a responsive layout with return navigation.
<img width="1199" height="756" alt="642b289e022901a99b5f68f1a5e94f4" src="https://github.com/user-attachments/assets/ee03551b-73a5-4565-862b-7016bc432df9" />

### Tech Stack
- Frontend: Vue 3, Pinia, Vite
- Backend: Ruoyi Framework (Spring Boot)
- Database: MySQL
- API: RESTful API
- Deployment: Nginx + Docker

## Project Structure

```
├── .gitignore                    # Git ignore file
├── LICENSE                       # Open source license
├── README.md                     # Project documentation
├── pom.xml                       # Root Maven configuration
├── ry.bat                        # Backend startup script (Windows)
├── ry.sh                         # Backend startup script (Linux/Mac)
├── backend-sql                   # MySQL database files
├── bin/                          # Shell/batch script directory
│   ├── clean.bat                 # Clean script
│   ├── package.bat               # Build/package script
│   └── run.bat                   # Run script
├── doc/                          # Documentation
│   └── ruoyi-code-generation-config   #Code generator export configuration
├── sql/                          # SQL files for database setup
│   ├── ry_20250522.sql           # RuoYi base tables
│   ├── coupon_activity_simplified.sql # prize, log, config, image_resource, food(2)
│   ├── README.sql.md             # Info of sql
│   └── quartz.sql                # Quartz scheduled tasks
├── rain-of-coupon/
│   ├── public/                   # Public static files
│   ├── docs/                     # Project-specific documentation
│   ├── package.json              # NPM dependencies and scripts
│   ├── vite.config.ts            # Vite configuration
│   ├── vercel.json               # Vercel deployment config
│   └── src/
│       ├── api/                  # API abstraction layer (Axios)
│       ├── assets/               # Static assets (mock data)
│       ├── components/           # Reusable UI components
│       ├── components/             # Reusable UI components
│       │   ├── PrizeModal.vue          # Modal when user wins a prize
│       │   ├── EncourageTip.vue        # Popup for encouragement on loss
│       │   ├── RedPacket.vue           # Red envelope animation logic
│       │   ├── CountDown.vue           # Countdown animation (3,2,1)
│       │   ├── RulePopup.vue           # Popup or static block for activity rules
│       │   ├── CouponCard.vue          # Single coupon display block (in /coupon)
│       │   ├── CrowdingTip.vue         # UI hint for "Too many users"
│       │   ├── LoadingAnim.vue         # Rocket animation / loading animation
│       │   ├── BackButton.vue          # Back button used in /coupon or others
│       │   ├── LoginForm.vue           # Encapsulated login form block
│       ├── composables/          # Vue 3 composable functions (e.g., useUser)
│       ├── directives/           # Custom Vue directives
│       ├── router/               # Vue Router setup
│       │   └── index.js              # Route definitions
│       ├── stores/               # State management (Pinia)
│       ├── utils/                # Utility functions
│       │   └── request.js            # Axios interceptor and config
│       ├── views/                # Page-level components (routes)
│       │   ├── HomePage.vue          # Landing page
│       │   ├── LoginPage.vue         # Login screen
│       │   ├── LoadingPage.vue       # Loading animation screen
│       │   ├── CountDownPage.vue     # Countdown page
│       │   ├── RedPacketPage.vue     # Red envelope draw page
│       │   ├── RulePage.vue          # Event rules page
│       │   └── CouponPage.vue        # Coupon/prize list page
│       ├── App.vue               # Root Vue component
│       └── main.js               # Application entry point
├── ruoyi-ui/
│   ├── src/
│   │   ├── api/                  # API definitions
│   │   ├── assets/               # Static assets
│   │   ├── components/           # UI components
│   │   ├── directive/            # Custom directives
│   │   ├── layout/               # Page layout system
│   │   ├── router/               # Vue Router configuration
│   │   ├── store/                # Vuex state management
│   │   ├── utils/                # Utility functions and helpers
│   │   └── views/                # Page views (admin)
│   ├── bin/                      # Admin start/build scripts
│   ├── package.json              # NPM configuration
│   └── vue.config.js             # Vue CLI configuration
├── ruoyi-admin/                  # Main backend module
│   ├── src/main/java/com/ruoyi/  # Java source code
│   ├── src/main/resources/       # Application configs
│   │   ├──image/redpacket        # Image resources
|   │   │   ├── README.image.md   # Info of image
|   │   │   ├── ...
│   └── pom.xml                   # Module-specific Maven config
├── ruoyi-common/                 # Shared utility module
├── ruoyi-framework/             
```

## Dev Progress

Phase 1 and Phase 2 may progress in parallel, with priority given to completing the MVP.

### Phase 1: Database Design & API Development (In Progress: 3 days)

#### ✅ **Completed Tasks:**

**Database Design** `8/5 - 8/6`

**Code Generation** `8/6`
- ✅ Entity classes generated
- ✅ Mapper interfaces generated  
- ✅ Basic CRUD Service and Controller layers generated
- ✅ Menu configuration SQL files generated

**Core Business Logic for the Lottery Controller and Service Layer**`8/6`
- ✅ Daily Limit of 3
- ✅ Lottery Stops After Winning
- ✅ IP Frequency Limit (10 Times per Hour)
- ✅ Event Duration Control
- ✅ Weighted Random Probability Algorithm
- ✅ Automatic Inventory Deductions
- ✅ Automatic Exclusion of Low Inventory
- ✅ Transactions Ensure Data Consistency


### Phase 2: Frontend Page Structure & API Integration (In Progress: 3 days)

```
Home (/)
├─ Click "Join Activity" → Login Page (/login)
│   ├─ After successful login → Check if user has already won
│   │   ├─ If already won → Show PrizeModal popup
│   │   ├─ If not yet won → Auto redirect → Loading Page (/loading)
│   │   │   ├─ If traffic is high → Show CrowdingTip component
│   │   │   ├─ If traffic is normal → Go to Countdown Page (/countdown)
│   │   │   │   ├─ After countdown → Redirect to RedPacket Page (/redpacket)
│   │   │   │   │   ├─ If user wins → Show PrizeModal popup
│   │   │   │   │   └─ If user doesn't win → Show EncourageTip popup
│   │   │   └─ After lottery draw → Return to Home (/)
├─ Click "Activity Rules" → Rules Page (/rule)
└─ After lottery ends or user logs in → Coupon Page (/coupon)
```
```
📦 Page-Level Route Structure
/
├── HomePage (/)
│   ├── Join Activity → LoginPage (/login)
│   ├── View Activity Rules → RulePage (/rule)
│   └── View My Coupons → CouponPage (/coupon)

├── LoginPage (/login)
│   └── On successful login → Check winning status
│       ├── If already won → show PrizeModal (component)
│       └── If not yet → redirect to LoadingPage (/loading)

├── LoadingPage (/loading)
│   ├── If crowded → show CrowdingTip (component)
│   └── If smooth → redirect to CountdownPage (/countdown)

├── CountdownPage (/countdown)
│   └── After countdown → redirect to RedPacketPage (/redpacket)

├── RedPacketPage (/redpacket)
│   ├── After user taps a red packet:
│   │   ├── If won → show PrizeModal (component)
│   │   └── If not → show EncourageTip (component)
│   └── After drawing → auto-return to HomePage

├── RulePage (/rule)
│   └── Display static activity rules content

├── CouponPage (/coupon)
│   └── Display received coupons + BackButton
```
- LoginPage.vue

1. User status detection (via token login)

2. Axios setup with request interceptors

- Red Envelope logic: *Falling red envelope animation*， *Click to trigger draw request*， *Popup to show win or no-win*
  
```
let totalRedPackets = 100;
let interval = setInterval(() => {
  if (totalRedPackets <= 0) clearInterval(interval);
  generateRedPacket(); // 每次生成1个红包动画
  totalRedPackets--;
}, 200);
```

- Detects high user traffic： "Current Limitation" Problem -> Backend current limiting + status response + 
Front-end loading judgment, return `{ "status": "crowded" } // or "ok"`

- Randomly determine whether the user has grabbed the red envelope
```
// Query today's lottery records
List<UserPrizeLog> logs = userPrizeLogMapper.queryToday(userId);
if (logs.size() >= 3) return fail("次数用尽");

boolean alreadyWon = logs.stream().anyMatch(log -> log.isWin());
if (alreadyWon) return fail("已中奖");

boolean isWin = Math.random() < 0.2; // 20% chance of winning, configurable in the background
if (isWin) {
    // Randomly distribute prizes
    Prize prize = prizeService.getRandomAvailablePrize();
    saveUserPrize(userId, prize);
}

```
Configurable prize distribution algorithm:

Use Redis for inventory deductions (to prevent concurrent over-issuance)

Prize probability is stored in a configurable field in the database

### Phase 3: Integration & Deployment Preparation (In progress: 1 days)

- Connect frontend and backend lottery logic

- Validate draw limits and winning logic

#### 📋 **API Specifications:**

| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/lottery/draw` | POST | Execute lottery draw | 🔄 In Progress |
| `/api/lottery/records` | GET | Get user draw history | 🔄 In Progress |
| `/api/lottery/drawCount` | GET | Get remaining draws | 🔄 In Progress |
| `/api/lottery/prizes` | GET | Get prize configuration | 🔄 In Progress |
| `/api/lottery/status` | GET | Check user eligibility | 🔄 In Progress |
| `/api/activity/config` | GET | Get activity settings | 🔄 In Progress |

## License
This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.
