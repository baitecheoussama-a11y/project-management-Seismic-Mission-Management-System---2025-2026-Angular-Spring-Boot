import { NbMenuItem } from '@nebular/theme';

export interface CustomMenuItem extends NbMenuItem {
  hiddenForRoles?: string[];
}
export const MENU_ITEMS: CustomMenuItem[] = [
  
    {
          hiddenForRoles: ["GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION","DIRECTEUR"],
    title: 'Dashboard',
       icon: 'home-outline',

    link: '/pages/dashboard2',
    home: true,
  },
 // ========================================
// 📊 DASHBOARDS
// ========================================
{
  hiddenForRoles: ["GESTIONNAIRE", "CHEF_TERRAIN", "CHEF_MISSION"],
  title: 'Dashboard',
  icon: 'home-outline',
  link: '/pages/ddashboard',
  home: true,
},
{
  hiddenForRoles: ["GESTIONNAIRE", "CHEF_TERRAIN", "CHEF_MISSION"],
  title: 'Analytics Dashboard',
  icon: 'bar-chart-outline',
  link: '/pages/analytics/dashboard',
},
{
  hiddenForRoles: ["GESTIONNAIRE", "CHEF_TERRAIN", "CHEF_MISSION"],
  title: 'Production Dashboard',
  icon: 'activity-outline',
  link: '/pages/stats/production',
},

// ========================================
// 📋 DATA ANALYSIS
// ========================================
{
  hiddenForRoles: ["GESTIONNAIRE", "CHEF_TERRAIN", "CHEF_MISSION"],
  title: 'Pivot Table',
  icon: 'grid-outline',
  link: '/pages/analytics/PivotTable',
},
  
  
    {
          hiddenForRoles: ['ADMIN',"DIRECTEUR"],
    title: 'Mission Dashboard',
    icon: 'home-outline',
    link: '/pages/mission-dashboard',
    home: true,
  },
     {
          hiddenForRoles: ['ADMIN',"DIRECTEUR"],
    title: 'Project Dashboard',
        icon: 'shopping-cart-outline',
    link: '/pages/project-dashboard',

  },


  
  {
    
    title: 'E-commerce',
    icon: 'shopping-cart-outline',
    link: '/pages/dashboard',
   
       hiddenForRoles: ['ADMIN',"GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION","DIRECTEUR"],

  },
  {
          hiddenForRoles: ["GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION","DIRECTEUR"],

    title: 'IoT Dashboard',
    icon: 'shopping-cart-outline',
    link: '/pages/iot-dashboard',
  },
  {
    title: 'FEATURES',
    group: true,
  
  },
  {
    title: 'Layout',
    icon: 'layout-outline',
       hiddenForRoles: ['ADMIN',"GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION","DIRECTEUR"],

    children: [
      {
        title: 'Stepper',
        link: '/pages/layout/stepper',
      },
      {
        title: 'List',
        link: '/pages/layout/list',
      },
      {
        title: 'Infinite List',
        link: '/pages/layout/infinite-list',
      },
      {
        title: 'Accordion',
        link: '/pages/layout/accordion',
      },
      {
        title: 'Tabs',
        pathMatch: 'prefix',
        link: '/pages/layout/tabs',
      },
    ],
  },
  {
    title: 'Forms',
        hiddenForRoles: ['ADMIN',"GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION","DIRECTEUR"],

    icon: 'edit-2-outline',
    children: [
      {
        title: 'Form Inputs',
        link: '/pages/forms/inputs',
      },
      {
        title: 'Form Layouts',
        link: '/pages/forms/layouts',
      },
      {
        title: 'Buttons',
        link: '/pages/forms/buttons',
      },
      {
        title: 'Datepicker',
        link: '/pages/forms/datepicker',
      },
    ],
  },
  {
    title: 'UI Features',
    icon: 'keypad-outline',
    link: '/pages/ui-features',
        hiddenForRoles: ['ADMIN',"GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION","DIRECTEUR"],

    children: [
      {
        title: 'Grid',
        link: '/pages/ui-features/grid',
      },
      {
        title: 'Icons',
        link: '/pages/ui-features/icons',
      },
      {
        title: 'Typography',
        link: '/pages/ui-features/typography',
      },
      {
        title: 'Animated Searches',
        link: '/pages/ui-features/search-fields',
      },
    ],
  },
  {
    title: 'Modal & Overlays',
    icon: 'browser-outline',
      hiddenForRoles: ['ADMIN',"GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION","DIRECTEUR"],

    children: [
      {
        title: 'Dialog',
        link: '/pages/modal-overlays/dialog',
      },
      {
        title: 'Window',
        link: '/pages/modal-overlays/window',
      },
      {
        title: 'Popover',
        link: '/pages/modal-overlays/popover',
      },
      {
        title: 'Toastr',
        link: '/pages/modal-overlays/toastr',
      },
      {
        title: 'Tooltip',
        link: '/pages/modal-overlays/tooltip',
      },
    ],
  },
  {
    title: 'Extra Components',
     hiddenForRoles: ['ADMIN',"GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION","DIRECTEUR"],

    icon: 'message-circle-outline',
    children: [
      {
        title: 'Calendar',
        link: '/pages/extra-components/calendar',
      },
      {
        title: 'Progress Bar',
        link: '/pages/extra-components/progress-bar',
      },
      {
        title: 'Spinner',
        link: '/pages/extra-components/spinner',
      },
      {
        title: 'Alert',
        link: '/pages/extra-components/alert',
      },
      {
        title: 'Calendar Kit',
        link: '/pages/extra-components/calendar-kit',
      },
      {
        title: 'Chat',
        link: '/pages/extra-components/chat',
      },
    ],
  },

  {
    title: 'Maps',
    icon: 'map-outline',

    
    children: [

       {
        title: 'Algeria Maps',
        link: '/pages/maps/algeria',
      },
      {
        title: 'Google Maps',
        link: '/pages/maps/gmaps',
      },
      {
        title: 'Leaflet Maps',
        link: '/pages/maps/leaflet',
      },
    
      {
        title: 'Search Maps',
        link: '/pages/maps/searchmap',
      },
    ],
  },
  {
    title: 'Charts',
     hiddenForRoles: ['ADMIN',"GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION","DIRECTEUR"],
     
    icon: 'pie-chart-outline',
    children: [
      {
        title: 'Echarts',
        link: '/pages/charts/echarts',
      },
      {
        title: 'Charts.js',
        link: '/pages/charts/chartjs',
      },
      {
        title: 'D3',
        link: '/pages/charts/d3',
      },
    ],
  },
  {
    title: 'Editors',
         hiddenForRoles: ['ADMIN',"GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION","DIRECTEUR"],

    icon: 'text-outline',
    children: [
      {
        title: 'TinyMCE',
        link: '/pages/editors/tinymce',
        
      },
      {
        title: 'CKEditor',
        link: '/pages/editors/ckeditor',
      },
    ],
  },
{
  title: 'Employes & Accounts',
  icon: 'grid-outline',
  hiddenForRoles: ['CHEF_MISSION', "GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION","DIRECTEUR"],
  children: [
    {
      title: 'Employes Table',
      icon: 'people-outline',
      link: '/pages/tables/smart-table',
    },
    {
      title: 'Medical Table',
      icon: 'heart-outline',
      link: '/pages/tables/etat-medical',
    },
{
  title: 'Fonctions Table',
  icon: 'briefcase-outline',  // Or use: 'grid-outline', 'layers-outline', 'options-2-outline'
  link: '/pages/tables/fonction-table',
},

    {
      title: 'Incidents',
      icon: 'alert-triangle-outline',
      link: '/pages/tables/incidents',
    },
    {
      title: 'Accounts Table',
      icon: 'person-done-outline',
      link: '/pages/tables/accounts',
    }
  ],
},

{
  title: 'Safety & Events',
  icon: 'shield-outline',
    hiddenForRoles: ["ADMIN","DIRECTEUR"],
  children: [
    {
  title: 'Attendance',
  icon: 'calendar-outline',
  link: '/pages/tables/pointage',
},
    {
      title: 'Incidents',
      icon: 'alert-triangle-outline',
      link: '/pages/safety/incidents',
    },
    {
      title: 'Events',
      icon: 'calendar-outline',
      link: '/pages/safety/events',
    },
    {
      title: ' Reports',
      icon: 'clipboard-outline',
           link: '/pages/safety/rapports',

    },
  ],
},

{
  title: 'Safety & Events',
  
  icon: 'shield-outline',
             hiddenForRoles: ["GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION",],

  children: [
       {
      title: 'Events',
      icon: 'calendar-outline',
      link: '/pages/safety/events',
    },

       {
  title: 'Attendance',
  icon: 'calendar-outline',
  link: '/pages/tables/pointage',
},

    {
      title: 'Incidents',
      icon: 'alert-triangle-outline',
      link: '/pages/safety/incidents',
    },
 
    
  
  ],
},


  {
          hiddenForRoles: ["ADMIN","DIRECTEUR"],

  title: 'Teams',
  icon: 'people-outline',
  children: [
    {
      title: 'Members',
      link: '/pages/teams/members',
    },
    {
      title: 'Activities',
      link: '/pages/teams/activities',
    },
      {
      title: 'Performance',
      link: '/pages/teams/Performance',
    },
   
  ],
},
{
  title: 'Materiels && Ressources',
  icon: 'hard-drive-outline',
  children: [
    {
      title: 'Materielt',
      icon: 'cube-outline',      // ✅ أيقونة جميلة
      link: '/pages/materiel',
    },
    {
      title: 'Ressources',
      icon: 'layers-outline',    // ✅ أيقونة جميلة
      link: '/pages/ressource',
    }
  ],
},
 {
    title: 'Tables & Data',
    icon: 'grid-outline',
 hiddenForRoles: ['admin'],
    children: [
  {
  title: 'Missions',
  icon: 'compass-outline',
  link: '/pages/tables/missions',
},
{
  title: 'Projects',
  icon: 'folder-outline',
  link: '/pages/project/overview',
},
 {
     title: 'Reports',
      icon: 'clipboard-outline',
      link: '/pages/tables/reports',

    },

      
    ],
  },
 
{
  title: 'My Account',
  icon: 'person-outline',
  children: [
    {
      title: 'Profile',
      icon: 'person-outline',
      link: '/pages/profile',
    },
    {
      title: 'Logout',
      icon: 'log-out-outline',
      data: { action: 'logout' }, // 👈 مهم جدا
    },
  ],
},

  {
    title: 'Miscellaneous',
     hiddenForRoles: ['ADMIN',"GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION","DIRECTEUR"],
    icon: 'shuffle-2-outline',
    children: [
      {
        title: '404',
        link: '/pages/miscellaneous/404',
      },
    ],
  },
  {
    title: 'Auth',
    icon: 'lock-outline',
         hiddenForRoles: ['ADMIN',"GESTIONNAIRE","CHEF_TERRAIN","CHEF_MISSION","DIRECTEUR"],

    children: [
      {
        title: 'Login',
        link: '/auth/login',
      },
      {
        title: 'Register',
        link: '/auth/register',
      },
      {
        title: 'Request Password',
        link: '/auth/request-password',
      },
      {
        title: 'Reset Password',
        link: '/auth/reset-password',
      },
    ],
  },
];
