const firebaseConfig = {
    apiKey: "AIzaSyCXUmVHM-Y5E-x2ai2POkZ6tNzTs4Ris08",
    authDomain: "myapp-a79c7.firebaseapp.com",
    projectId: "myapp-a79c7",
    storageBucket: "myapp-a79c7.appspot.com",
    messagingSenderId: "941693994437",
    appId: "1:941693994437:web:202be043aaf8dc34f577ac"
  };
  
  let db;
  
  try {
    if (!firebase.apps.length) {
      firebase.initializeApp(firebaseConfig);
    } else {
      firebase.app(); 
    }
  
    db = firebase.firestore();
  
    db.settings({
      ignoreUndefinedProperties: true,
      experimentalForceLongPolling: true
    });
  
    console.log("Firebase initialized successfully");
    showToast("تم الاتصال بالخادم بنجاح", "success");
  
    setupConnectionHandlers();
  
    if (navigator.onLine) {
      syncOfflineData();
    }
  } catch (error) {
    console.error("Firebase initialization error:", error);
    showToast("فشل الاتصال بالخادم", "error");
    setupOfflineMode();
  }
  