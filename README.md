## Red Envelope Rain Mini Program
Developed a WeChat mini program using Vue 3 and the Ruoyi Framework. Key features include:

- Lottery system: Up to 3 draws per user per day; ends automatically upon winning.

- Traffic control: Detects high user traffic and displays congestion notifications.

- Interactive animations: Falling red envelopes and clickable reward triggers.

- Coupon package page: Designed a responsive layout with return navigation.

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
│   └── RuoYi User Manual.docx    # Manual for setting up RuoYi framework
├── sql/                          # SQL files for database setup
│   ├── ry_20250522.sql           # RuoYi base tables
│   ├──  
│   ├──  
│   ├──  
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
│       │   ├── PrizeModal.vue        # Modal when user wins a prize
│       │   ├── EncourageTip.vue      # Popup for encouragement on loss
│       │   ├── RedPacket.vue         # Red envelope animation
│       │   ├── CountDown.vue         # Countdown animation (3,2,1)
│       │   └── RulePopup.vue         # Rules popup (optional)
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

# ✅ RuoYi Backend Modules (Spring Boot)
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

### Phase 1: Database Design & API Development (In Progress:2 days)

- Design tables:`8/5 - present`

1.  prize – Prize configuration: Prize name, total number of prizes, remaining quantity, winning probability

2. user_prize_log – User draw records: User ID, prize name, winning time, usage status

3. image_resource - Static assets: Resource name, resource identifier, file name, file access path, usage scenario, description

4. user_info - (Ruoyi-embeded): Username, password, phone number

5. Top 10 popular online specialties: Dish name, ranking

6. "One Town, One Product" specialties: Dish name, ranking

- Use Ruoyi's built-in code generator to generate

1. Entity classes

2. Mapper interfaces

3. Service and Controller layers

- Develop APIs:

- Draw lottery: POST /api/draw

- View draw records: GET /api/user/records

- Check remaining draws: GET /api/user/drawCount

- Get prize pool config: GET /api/prizes

### Phase 2: Frontend Page Structure & API Integration (In Progress: 3 days)

```

Home (/)
├─ Click “Join Activity” → Login Page (/login)
  ├─ After successful login → Loading Page (/loading)
    ├─ Auto redirect → Countdown Page (/countdown)
      ├─ When countdown ends → Red Packet Page (/redpacket)
        ├─ If user wins → Show PrizeModal popup
        └─ If user loses → Show EncourageTip popup
      └─ After the draw → Return to Home (/)
├─ Click “Event Rules” → Rules Page (/rule)
└─ After draw ends or user logs in → Coupon Page (/coupon)

```
- LoginPage.vue

1. User status detection (via token login)

2. Axios setup with request interceptors

- HomePage.vue

1. Components(Overlay): CountDown, RedPacketRain, CouponModal, Rulepop Up, EncourageTip, CrowdingTip

2. Red Envelope logic: *Falling red envelope animation*， *Click to trigger draw request*， *Popup to show win or no-win*

### Phase 3: Integration & Deployment Preparation (In progress: 1 days)

- Connect frontend and backend lottery logic

- Validate draw limits and winning logic


## License
This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.
