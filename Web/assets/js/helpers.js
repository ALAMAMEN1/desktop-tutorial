// عرض رسائل التنبيه
function showToast(message, type = "info") {
    const toast = document.getElementById("toast");
    toast.textContent = message;
    toast.className = "show";
    
    toast.classList.remove("toast-success", "toast-error", "toast-warning", "toast-info");
    
    switch(type) {
      case "success":
        toast.classList.add("toast-success");
        break;
      case "error":
        toast.classList.add("toast-error");
        break;
      case "warning":
        toast.classList.add("toast-warning");
        break;
      case "info":
        toast.classList.add("toast-info");
        break;
    }
    
    setTimeout(() => {
      toast.classList.remove("show");
    }, 3000);
}

async function generateQRCode(elementId, text) {
  try {
    const canvas = document.getElementById(elementId);
    
    if (canvas) {
      const context = canvas.getContext('2d');
      context.clearRect(0, 0, canvas.width, canvas.height);
    }
    
    await QRCode.toCanvas(canvas, text, {
      width: 150,
      margin: 1,
      color: {
        dark: "#3a0ca3",
        light: "#ffffff"
      }
    });
  } catch (error) {
    console.error("QR Code generation error:", error);
    const container = document.getElementById(elementId).parentElement;
    container.innerHTML = `
      <div class="qr-error">
        <i class="fas fa-exclamation-triangle"></i>
        <p>تعذر إنشاء QR Code</p>
      </div>
    `;
  }
}

function setupConnectionHandlers() {
  const statusElement = document.getElementById("connection-status");

  function updateStatus() {
    if (navigator.onLine) {
      statusElement.innerHTML = `<i class="fas fa-wifi"></i> متصل بالإنترنت`;
      statusElement.className = "connected";

      checkFirebaseConnection().then(isConnected => {
        if (isConnected) {
          showToast("تم استعادة الاتصال بالخادم", "success");
          document.getElementById("offline-notification").classList.add("hidden");
          if (document.getElementById("sessions").classList.contains("active")) {
            loadSessions();
          }
        } else {
          statusElement.innerHTML = `<i class="fas fa-unlink"></i> اتصال بدون خادم`;
          statusElement.className = "disconnected";
        }
      });
    } else {
      statusElement.innerHTML = `<i class="fas fa-wifi-slash"></i> غير متصل`;
      statusElement.className = "disconnected";
      showToast("فقدان الاتصال بالإنترنت", "warning");
      document.getElementById("offline-notification").classList.remove("hidden");
    }
  }

  window.addEventListener("online", updateStatus);
  window.addEventListener("offline", updateStatus);
  updateStatus();
}

async function syncOfflineData() {
  const offlineSessions = JSON.parse(localStorage.getItem("offlineSessions") || "[]");

  if (offlineSessions.length > 0) {
    showToast("جاري مزامنة البيانات المحلية...", "info");

    try {
      for (const session of offlineSessions) {
        await db.collection("sessions").add({
          subject: session.subject,
          teacher: session.teacher,
          date: session.date,
          location: session.location,
          students: session.students,
          entryCode: session.entryCode,
          exitCode: session.exitCode,
          createdAt: firebase.firestore.FieldValue.serverTimestamp()
        });
      }

      localStorage.removeItem("offlineSessions");
      showToast(`تمت مزامنة ${offlineSessions.length} حصة`, "success");
      if (document.getElementById("sessions").classList.contains("active")) {
        loadSessions();
      }
    } catch (error) {
      console.error("Error syncing offline data:", error);
      showToast("فشل في مزامنة بعض البيانات", "error");
    }
  }
}

function setupOfflineMode() {
  const container = document.querySelector(".container");
  const offlineHTML = `
    <div class="offline-mode">
      <div class="offline-content">
        <i class="fas fa-wifi-slash"></i>
        <h2>لا يوجد اتصال بالخادم</h2>
        <p>التطبيق يعمل في الوضع المحلي حالياً</p>
        <div class="offline-actions">
          <button onclick="checkConnection()">
            <i class="fas fa-sync-alt"></i> إعادة المحاولة
          </button>
        </div>
      </div>
    </div>
  `;
  container.innerHTML = offlineHTML;
  document.getElementById("connection-status").className = "disconnected";
}

function checkConnection() {
  showToast("جاري التحقق من الاتصال...", "info");

  if (!navigator.onLine) {
    showToast("لا يزال الاتصال بالإنترنت مقطوعاً", "error");
    return;
  }

  checkFirebaseConnection().then(isConnected => {
    if (isConnected) {
      showToast("تم استعادة الاتصال بالخادم", "success");
      window.location.reload();
    } else {
      showToast("الخادم غير متاح حالياً", "error");
    }
  });
}

function generateCode() {
  return Math.floor(1000 + Math.random() * 9000).toString();
}

function showEmptyState(message = "لا توجد حصص حالية") {
  const sessionsList = document.getElementById("sessionsList");
  sessionsList.innerHTML = `
    <div class="empty-state">
      <i class="fas fa-calendar-times"></i>
      <p>${message}</p>
    </div>
  `;
}

function checkFirebaseConnection() {
  return new Promise((resolve) => {
    const testDoc = db.collection("connectionTest").doc("test");
    testDoc.get()
      .then(() => resolve(true))
      .catch(() => resolve(false));
  });
}
