🌐 Language Switch | 语言切换:  [English](./README.md) | [中文](./README.cn.md)

## Red Envelope Rain Mini Program
Developed a mini program using Vue 3 and the Ruoyi Framework. Key features include:

- **Red Envelope Rain System**: Up to 3 games per user per day; ends automatically upon winning. The more red envelopes clicked, the higher the winning probability.

- Traffic control: Detects high user traffic and displays congestion notifications.

- Interactive animations: Falling red envelopes and clickable reward triggers.

- Coupon package page: Designed a responsive layout with return navigation.
<img width="1199" height="756" alt="642b289e022901a99b5f68f1a5e94f4" src="https://github.com/user-attachments/assets/ee03551b-73a5-4565-862b-7016bc432df9" />

### Tech Stack
- Frontend: Vue 2, Vuex, Vite
- Backend: Ruoyi Framework (Spring Boot)
- Database: MySQL
- API: RESTful API
- Deployment: Nginx + Docker

## Database Schema

### Core Business Tables
1. **redpacket_prize** (Prize Configuration Table)
   - Prize name, total quantity, remaining quantity, win probability
2. **redpacket_user_participation_log** (User Participation Log Table)
   - User ID, IP address, win status, participation time, prize info
3. **redpacket_event_config** (Event Configuration Table)
   - Event duration, concurrency limits, daily game limits
4. **image_resource** (Image Resource Table)
   - Static image resource management for red envelope rain

### Business Features
- **Anti-Fraud Mechanism**: IP frequency limits, daily attempt limits, stop after winning
- **Probability Algorithm**: Dynamic probability calculation based on click count
- **Concurrency Control**: Redis cache + database transactions ensure data consistency

## Project Structure

```
├── .gitignore                    # Git ignore rules
├── LICENSE                       # Open-source license
├── README.md                     # Project documentation
├── pom.xml                       # Root-level Maven configuration
├── ry.bat                        # Backend startup script (Windows)
├── ry.sh                         # Backend startup script (Linux/Mac)
├── backend-sql                   # MySQL database files
├── bin/                          # Shell/batch script directory
│   ├── clean.bat                 # Clean-up script
│   ├── package.bat               # Build/package script
│   └── run.bat                   # Run/start script
├── doc/                          # Documentation directory
│   └── ruoyi-code-generation-config   # Export config for code generator
├── sql/                          # SQL scripts for database initialization
│   ├── ry_20250522.sql               # Base tables for RuoYi framework
│   ├── coupon_activity_simplified.sql # Tables: prize, log, config, image, food
│   ├── add-participation-log-table.sql # Red envelope rain user participation log table
│   ├── business_log.sql              # Business log table
│   ├── README.sql.md                 # Description of SQL files (includes red envelope rain table structure)
│   └── quartz.sql                    # Quartz scheduled task tables
├── rain-of-coupon/              # Frontend project (e.g., mini-game or web)
│   ├── public/                   # Public static resources
│   ├── docs/                     # Project-specific documents
│   ├── package.json              # NPM dependencies and scripts
│   ├── vercel.json               # Vercel deployment config (optional)
│   └── src/
│       ├── api/                  # API abstraction layer (based on Axios)
│       ├── assets/               # Static assets (images, mock data, etc.)
│       ├── components/           # Reusable Vue components
│       │   ├── PrizeModal.vue          # Modal displayed on prize win
│       │   ├── EncourageTip.vue        # Encouragement popup when user loses
│       │   ├── RedPacket.vue           # Red envelope animation logic
│       │   ├── CountDown.vue           # Countdown animation (3, 2, 1)
│       │   ├── RulePopup.vue           # Rule popup or static display
│       │   ├── CouponCard.vue          # Coupon display component (for /coupon)
│       │   ├── CrowdingTip.vue         # "Too many users" warning UI
│       │   ├── LoadingAnim.vue         # Rocket or loading animation
│       │   ├── BackButton.vue          # Back button used in pages
│       │   ├── LoginForm.vue           # Login form block
│       ├── directives/           # Custom Vue directives (e.g., auto-focus)
│       ├── router/               # Vue Router configuration
│       │   └── index.js              # Route definitions
│       ├── store/                # Vuex store configuration
│       │   ├── index.js              # Vuex main entry
│       │   └── modules/              # Vuex modules
│       ├── utils/                # Utility functions
│       │   └── request.js            # Axios configuration and interceptors
│       ├── views/                # Page-level components (linked to routes)
│       │   ├── HomePage.vue          # Home / landing page
│       │   ├── LoginPage.vue         # Login screen
│       │   ├── LoadingPage.vue       # Loading animation screen
│       │   ├── CountDownPage.vue     # Countdown screen
│       │   ├── RedPacketPage.vue     # Red envelope draw screen
│       │   ├── RulePage.vue          # Event rules page
│       │   └── CouponPage.vue        # Coupon / prize list page
│       ├── App.vue               # Root Vue component
│       └── main.js               # Application entry point (Vue 2 + Vuex init)
├── ruoyi-ui/                    # Admin frontend (Vue 2 based)
│   ├── src/
│   │   ├── api/                  # API definitions for backend
│   │   ├── assets/               # Static assets
│   │   ├── components/           # Common UI components
│   │   ├── directive/            # Custom Vue directives
│   │   ├── layout/               # Layout system (headers, sidebar, etc.)
│   │   ├── router/               # Routing configuration
│   │   ├── store/                # Vuex state management
│   │   ├── utils/                # Utility functions and helpers
│   │   └── views/                # Admin page views
│   ├── bin/                      # Admin build/start scripts
│   ├── package.json              # NPM config for admin
│   └── vue.config.js             # Vue CLI configuration
├── ruoyi-admin/                 # Main backend module
│   ├── src/main/java/com/ruoyi/  # Java source code
│   ├── src/main/resources/       # Config files and resources
│   │   ├──image/redpacket        # Red packet image resources
│   │   │   ├── README.image.md   # Image description file
│   │   │   ├── ...
│   └── pom.xml                   # Maven config specific to this module
├── ruoyi-common/                # Shared Java utility module
├── ruoyi-framework/             # Core backend framework module            
```
## Userflow & Page-Level Route Structure
1. 
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
2. 
```
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
## Dev Progress

Phase 1 and Phase 2 may progress in parallel, with priority given to completing the MVP.

### Phase 1: Database Design & API Development (In Progress: 3 days)

#### ✅ **Completed Tasks:**

**Database Design** `8/5 - 8/6``8/7 Supplemented the participation log table`

**Code Generation** `8/6`
- ✅ Entity classes generated
- ✅ Mapper interfaces generated  
- ✅ Basic CRUD Service and Controller layers generated
- ✅ Menu configuration SQL files generated

**Core Business Logic for Red Envelope Rain Controller and Service Layer**`8/6`
- ✅ Daily Limit of 3 Games
- ✅ Participation Stops After Winning (One Win Per Person)
- ✅ IP Frequency Limit (10 Times per Hour)
- ✅ Event Duration Control
- ✅ Click-Based Probability Algorithm (More Clicks = Higher Win Rate)
- ✅ Red Envelope Rain Interactive Mode (100 Red Envelopes Fall in 50 Seconds)
- ✅ Automatic Inventory Deductions
- ✅ Automatic Exclusion of Zero Inventory
- ✅ Transactions Ensure Data Consistency

***API Interface Specification** `8/6`
1. POST /api/lottery/draw ✅
- Executes red envelope rain logic
- Calculates win probability based on click count
- Checks user eligibility
- Saves participation record
- Returns red envelope grab results
2. GET /api/lottery/records ✅
- Gets the user's historical participation records
- Requires user login
3. GET /api/lottery/drawCount ✅
- Gets the remaining number of games
- Checks if a prize has been won
- Returns whether user can participate in red envelope rain
4. GET /api/lottery/prizes ✅
- Gets a list of all available prizes
- Automatically filters prizes with zero inventory
5. GET /api/lottery/status ✅
- Checks user eligibility for red envelope rain
- Returns detailed status information
- Including the reason for not being able to participate
6. GET /api/activity/config ✅
- Gets activity configuration information
- Activity duration, restrictions, etc.
- Determines activity status

### Phase 2: Frontend Page Structure & API Integration (In Progress: 3 days)

[README.frontend.md](doc\frontend-development-guide.md)

- **LoginPage.vue**

1. User status detection (via token login)

2. Axios setup with request interceptors

- **Red Envelope Rain Logic**: *100 red envelopes fall in 50 seconds*, *Click to accumulate count*, *Calculate win probability based on click count after game ends*, *Popup to show win or no-win*
  
```
let totalRedPackets = 100;
let interval = setInterval(() => {
  if (totalRedPackets <= 0) clearInterval(interval);
  generateRedPacket(); // 每次生成1个红包动画
  totalRedPackets--;
}, 200);
```

- **Detects high user traffic**： "Current Limitation" Problem -> Backend current limiting + status response + 
Front-end loading judgment, return `{ "status": "crowded" } // or "ok"`

- **Determine win based on click count**:

```java
// Query today's participation records
List<UserPrizeLog> logs = userPrizeLogMapper.queryToday(userId);
if (logs.size() >= 3) return fail("No attempts left");

boolean alreadyWon = logs.stream().anyMatch(log -> log.isWin());
if (alreadyWon) return fail("Already won");

// Calculate win probability based on click count
int clickedCount = request.getClickedCount(); // Number of red envelopes clicked
double baseProbability = 0.05; // 5% base probability
double probabilityMultiplier = 1.0 + Math.log(clickedCount) / Math.log(10) * 0.8;
double finalProbability = baseProbability * Math.min(probabilityMultiplier, 4.0);

boolean isWin = Math.random() < finalProbability;
if (isWin) {
    // Randomly distribute prizes
    Prize prize = prizeService.getRandomAvailablePrize();
    saveUserPrize(userId, prize);
}
```
- **Configurable prize distribution algorithm**:

1. Use Redis for inventory deductions (to prevent concurrent over-issuance)

2. Prize probability is stored in a configurable field in the database

### Phase 3: Integration & Deployment Preparation (In progress: 1 days)

- Connect frontend and backend lottery logic

- Validate draw limits and winning logic

## License
This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.
