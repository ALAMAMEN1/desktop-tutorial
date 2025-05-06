function showTab(tabId) {
  document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
  document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));

  document.getElementById(tabId).classList.add('active');
  document.querySelector(`button[onclick="showTab('${tabId}')"]`).classList.add('active');

  if (tabId === 'sessions') loadSessions();
}

// ============== إدارة الموقع ==============
function getLocation() {
  const locationInput = document.getElementById('location');
  locationInput.value = 'جاري تحديد الموقع...';

  if (!navigator.geolocation) {
    showAlert('error', 'المتصفح لا يدعم خدمة الموقع');
    locationInput.value = '';
    return;
  }

  navigator.geolocation.getCurrentPosition(
    (position) => {
      const lat = position.coords.latitude.toFixed(6);
      const lng = position.coords.longitude.toFixed(6);
      locationInput.value = `${lat},${lng}`;
    },
    (error) => {
      locationInput.value = '';
      let message = 'تعذر الحصول على الموقع';
      switch(error.code) {
        case error.PERMISSION_DENIED:
          message = 'تم رفض طلب الوصول إلى الموقع';
          break;
        case error.TIMEOUT:
          message = 'انتهت المهلة أثناء محاولة تحديد الموقع';
          break;
      }
      showAlert('error', message);
    },
    { enableHighAccuracy: true, timeout: 10000 }
  );
}

// ============== إدارة الحصص ==============
async function addSession() {
  const subject = document.getElementById('subject').value.trim();
  const teacher = document.getElementById('teacher').value.trim();
  const date = document.getElementById('date').value;
  const location = document.getElementById('location').value.trim();
  const studentsText = document.getElementById('students').value.trim();

  if (!subject || !teacher || !date || !location || !studentsText) {
    showAlert('error', 'الرجاء تعبئة جميع الحقول المطلوبة');
    return;
  }

  const students = studentsText.split('\n').filter(s => s.trim());
  if (students.length === 0) {
    showAlert('error', 'الرجاء إدخال أرقام الطلاب');
    return;
  }

  const studentsObj = {};
  students.forEach(student => {
    studentsObj[student.trim()] = {
      status: 'absent',
      entryTime: null,
      exitTime: null
    };
  });

  try {
    await db.collection('sessions').add({
      subject,
      teacher,
      date: new Date(date).getTime(),
      location,
      students: studentsObj,
      entryCode: generateCode(),
      exitCode: generateCode(),
      createdAt: firebase.firestore.FieldValue.serverTimestamp()
    });

    showAlert('success', 'تم إنشاء الحصة بنجاح');
    resetForm();
    showTab('sessions');
  } catch (error) {
    showAlert('error', handleFirebaseError(error));
  }
}

function generateCode() {
  return Math.floor(1000 + Math.random() * 9000).toString();
}

async function loadSessions() {
  const loading = document.getElementById('loading');
  const sessionsList = document.getElementById('sessionsList');

  loading.style.display = 'block';
  sessionsList.innerHTML = '';

  try {
    const snapshot = await db.collection('sessions')
      .orderBy('date', 'desc')
      .get();

    if (snapshot.empty) {
      sessionsList.innerHTML = '<p class="no-sessions">لا توجد حصص حالية</p>';
      return;
    }

    const now = Date.now();
    const activeSessions = [];

    snapshot.forEach(doc => {
      const session = doc.data();
      const twentyFourHours = 24 * 60 * 60 * 1000;
    if (now - session.date <= twentyFourHours) {
        activeSessions.push({ id: doc.id, ...session });
      } else {
        db.collection('sessions').doc(doc.id).delete();
      }
    });

    if (activeSessions.length === 0) {
      sessionsList.innerHTML = '<p class="no-sessions">لا توجد حصص حالية</p>';
      return;
    }

    activeSessions.forEach(session => {
      const sessionDiv = document.createElement('div');
      sessionDiv.className = 'session-card';
      sessionDiv.innerHTML = createSessionHTML(session);
      sessionsList.appendChild(sessionDiv);

      generateQRCodes(session.id, session.entryCode, session.exitCode);
    });
  } catch (error) {
    sessionsList.innerHTML = `
      <div class="error">
        <p>حدث خطأ أثناء تحميل الحصص</p>
        <p><small>${handleFirebaseError(error)}</small></p>
        <button onclick="loadSessions()">إعادة المحاولة</button>
      </div>
    `;
  } finally {
    loading.style.display = 'none';
  }
}

function createSessionHTML(session) {
  return `
    <h3>${session.subject}</h3>
    <p><strong>المحاضر:</strong> ${session.teacher}</p>
    <p><strong>الوقت:</strong> ${new Date(session.date).toLocaleString('ar-EG')}</p>
    <p><strong>الموقع:</strong> ${session.location}</p>
    
    <div class="qr-codes">
      <div>
        <p>كود الدخول: ${session.entryCode}</p>
        <canvas id="entry-${session.id}"></canvas>
      </div>
      <div>
        <p>كود الخروج: ${session.exitCode}</p>
        <canvas id="exit-${session.id}"></canvas>
      </div>
    </div>
    
    <div class="students-list">
      <h4>الطلاب المسجلين: ${Object.keys(session.students).length}</h4>
      <button onclick="viewStudents('${session.id}')">عرض قائمة الطلاب</button>
    </div>
  `;
}

async function generateQRCodes(sessionId, entryCode, exitCode) {
  try {
    await new Promise(resolve => setTimeout(resolve, 100));

    const entryCanvas = document.getElementById(`entry-${sessionId}`);
    const exitCanvas = document.getElementById(`exit-${sessionId}`);
    if (!entryCanvas || !exitCanvas) return;

    // الحصول على الإحداثيات من input أو من session (حسب السياق)
    const sessionDoc = await db.collection("sessions").doc(sessionId).get();
    const sessionData = sessionDoc.data();
    let lat = "0", lng = "0";

    if (sessionData && sessionData.location) {
      if (typeof sessionData.location === "string" && sessionData.location.includes(',')) {
        [lat, lng] = sessionData.location.split(',').map(x => x.trim());
      } else if (typeof sessionData.location === "object") {
        lat = sessionData.location.lat;
        lng = sessionData.location.lng;
      }
    }

    const entryUrl = `${window.location.origin}/attend.html?session=${sessionId}&type=entry&code=${entryCode}&lat=${lat}&lng=${lng}`;
    const exitUrl  = `${window.location.origin}/attend.html?session=${sessionId}&type=exit&code=${exitCode}&lat=${lat}&lng=${lng}`;

    await Promise.all([
      QRCode.toCanvas(entryCanvas, entryUrl, { width: 150 }),
      QRCode.toCanvas(exitCanvas, exitUrl, { width: 150 })
    ]);
  } catch (error) {
    console.error('خطأ في إنشاء رموز QR:', error);
    const container = document.querySelector(`#entry-${sessionId}`).parentElement;
    container.innerHTML += '<p class="error">تعذر إنشاء رمز QR</p>';
  }
}



function resetForm() {
  document.getElementById('subject').value = '';
  document.getElementById('teacher').value = '';
  document.getElementById('date').value = '';
  document.getElementById('location').value = '';
  document.getElementById('students').value = '';
}

function showAlert(type, message) {
  const alertDiv = document.createElement('div');
  alertDiv.className = `alert ${type}`;
  alertDiv.textContent = message;
  document.body.appendChild(alertDiv);

  setTimeout(() => alertDiv.remove(), 3000);
}

window.onload = () => showTab('add');

// ============== عرض الطلاب داخل نافذة ==============
async function viewStudents(sessionId) {
  try {
    const sessionRef = db.collection("sessions").doc(sessionId);
    const sessionDoc = await sessionRef.get();

    if (!sessionDoc.exists) {
      showToast("الحصة غير موجودة", "error");
      return;
    }

    const sessionData = sessionDoc.data();
    const students = sessionData.students || {};
    
    if (Object.keys(students).length === 0) {
      showToast("لا يوجد طلاب مسجلين", "info");
      return;
    }

    // إنشاء محتوى الجدول
    const tableRows = Object.entries(students).map(([id, data]) => {
      let status = 'غائب';
      let statusClass = 'status-absent';
      
      if (data.entryTime && data.exitTime) {
        if (data.entryTime < data.exitTime) {
          status = 'حاضر';
          statusClass = 'status-present';
        } else {
          status = 'خرج مبكراً';
          statusClass = 'status-partial';
        }
      } else if (data.entryTime) {
        status = 'دخل فقط';
        statusClass = 'status-partial';
      } else if (data.exitTime) {
        status = 'خرج فقط';
        statusClass = 'status-partial';
      }
      
      return `
        <tr>
          <td>${id}</td>
          <td><span class="status-badge ${statusClass}">${status}</span></td>
          <td>${data.entryTime ? new Date(data.entryTime).toLocaleString('ar-EG') : '--'}</td>
          <td>${data.exitTime ? new Date(data.exitTime).toLocaleString('ar-EG') : '--'}</td>
        </tr>
      `;
    }).join('');

    // إنشاء هيكل النافذة المنبثقة
    const modalHtml = `
      <div class="modal-overlay">
        <div class="modal-box">
          <div class="modal-header">
            <h3>قائمة الطلاب - ${sessionData.subject || 'غير معروف'}</h3>
            <button class="modal-close">&times;</button>
          </div>
          <div class="modal-body">
            <div class="session-info">
              <p><strong>المحاضر:</strong> ${sessionData.teacher || 'غير معروف'}</p>
              <p><strong>الوقت:</strong> ${new Date(sessionData.date).toLocaleString('ar-EG')}</p>
              <p><strong>عدد الطلاب:</strong> ${Object.keys(students).length}</p>
            </div>
            
            <table class="student-table">
              <thead>
                <tr>
                  <th>رقم الطالب</th>
                  <th>الحالة</th>
                  <th>وقت الدخول</th>
                  <th>وقت الخروج</th>
                </tr>
              </thead>
              <tbody>
                ${tableRows}
              </tbody>
            </table>
          </div>
          <div class="modal-footer">
            <button id="export-btn">تصدير البيانات</button>
          </div>
        </div>
      </div>
    `;

    // إضافة النافذة إلى DOM
    const wrapper = document.createElement('div');
    wrapper.innerHTML = modalHtml;
    document.body.appendChild(wrapper);

    // إضافة معالجات الأحداث
    wrapper.querySelector('.modal-close').addEventListener('click', () => {
      wrapper.remove();
    });

    wrapper.querySelector('.modal-overlay').addEventListener('click', (e) => {
      if (e.target === wrapper.querySelector('.modal-overlay')) {
        wrapper.remove();
      }
    });

    // معالج تصدير البيانات
    document.getElementById('export-btn').addEventListener('click', () => {
      exportStudentsData(sessionData, students);
    });

  } catch (error) {
    console.error("Error loading students:", error);
    showToast("فشل في جلب بيانات الطلاب", "error");
  }
}

// دالة مساعدة لتصدير البيانات
function exportStudentsData(sessionData, students) {
  let csvContent = "رقم الطالب,الحالة,وقت الدخول,وقت الخروج\n";
  
  Object.entries(students).forEach(([id, data]) => {
    let status = 'غائب';
    
    if (data.entryTime && data.exitTime) {
      status = data.entryTime < data.exitTime ? 'حاضر' : 'خرج مبكراً';
    } else if (data.entryTime) {
      status = 'دخل فقط';
    } else if (data.exitTime) {
      status = 'خرج فقط';
    }
    
    const entryTime = data.entryTime ? new Date(data.entryTime).toLocaleString('ar-EG') : '';
    const exitTime = data.exitTime ? new Date(data.exitTime).toLocaleString('ar-EG') : '';
    
    csvContent += `${id},${status},${entryTime},${exitTime}\n`;
  });
  
  const blob = new Blob(["\uFEFF" + csvContent], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = `طلاب_${sessionData.subject || 'حصة'}_${new Date(sessionData.date).toLocaleDateString('ar-EG')}.csv`;
  link.click();
  URL.revokeObjectURL(url);
}