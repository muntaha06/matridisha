let currentTrimester = '';
let jsonData = {};

// ১. JSON লোড করার ফাংশন
async function loadTrimesterData(trimester) {
    let fileName = "";

    if (trimester === "trimester1") fileName = "data/first_trimester.json";
    else if (trimester === "trimester2") fileName = "data/second_trimester.json";
    else if (trimester === "trimester3") fileName = "data/third_trimester.json";

    try {
        const res = await fetch(fileName);
        if (!res.ok) throw new Error("ফাইল খুঁজে পাওয়া যায়নি!");
        jsonData = await res.json();
        console.log("Data Loaded:", jsonData);
        
        // বিশেষ গুরুত্ব দেখানো
        showImportance();
    } catch (err) {
        console.error("JSON load error:", err);
        jsonData = {}; // খালি অবজেক্ট সেট করা
        document.getElementById('importance-container').style.display = 'none';
    }
}

// ২. বিশেষ গুরুত্ব দেখানোর ফাংশন
function showImportance() {
    const importanceContainer = document.getElementById('importance-container');
    const specialImportance = document.getElementById('special-importance');
    
    if (jsonData && jsonData['বিশেষ_গুরুত্ব']) {
        specialImportance.innerText = jsonData['বিশেষ_গুরুত্ব'];
        importanceContainer.style.display = 'block';
    } else {
        importanceContainer.style.display = 'none';
    }
}

// ৩. Trimester বাটন ক্লিক
async function showOptions(tri){
    currentTrimester = tri;
    
    // ডাটা লোড
    await loadTrimesterData(tri);

    document.getElementById('home-screen').style.display = 'none';
    document.getElementById('option-screen').style.display = 'block';
    document.getElementById('back-btn').style.display = 'inline-block';
}

// ৪. Food / Exercise / Caution দেখানোর ফাংশন
function showData(type){
    document.getElementById('option-screen').style.display = 'none';
    document.getElementById('data-screen').style.display = 'block';

    const displayArea = document.getElementById('display-area');
    displayArea.innerHTML = "";

    let categorizedItems = {};

    if (type === 'food') {
        const foodList = jsonData['খাদ্য_তালিকা'] || {};
        
        Object.keys(foodList).forEach(categoryName => {
            const category = foodList[categoryName];
            
            if (categoryName === 'পানি' && category.বিস্তারিত_কারণ) {
                if (!categorizedItems['💧 পানি']) categorizedItems['💧 পানি'] = [];
                categorizedItems['💧 পানি'].push({
                    নাম: 'পানি (' + (category.পরিমাণ || '') + ')',
                    বিস্তারিত_কারণ: category.বিস্তারিত_কারণ
                });
            }
            else if (Array.isArray(category)) {
                let icon = '🍚';
                if (categoryName === 'আমিষ') icon = '🍗';
                else if (categoryName === 'স্নেহ_জাতীয়_খাবার') icon = '🥑';
                else if (categoryName === 'ভিটামিন') icon = '🍊';
                else if (categoryName === 'খনিজ_লবণ') icon = '🥜';
                
                const displayName = icon + ' ' + categoryName;
                if (!categorizedItems[displayName]) categorizedItems[displayName] = [];
                
                category.forEach(item => {
                    let name = item.নাম || item.name || 'নাম নেই';
                    let image = item.ছবি || item.image || '';
                    let reasons = item.বিস্তারিত_কারণ || item.detailed_reasons || [];
                    
                    categorizedItems[displayName].push({
                        নাম: name,
                        ছবি: image,
                        বিস্তারিত_কারণ: reasons
                    });
                });
            }
        });
    } 
    else if (type === 'exercise') {
        categorizedItems['🏃‍♀️ ব্যায়াম'] = [];
        const exercises = jsonData['প্রতিদিনের_ব্যায়াম'] || [];
        
        exercises.forEach(item => {
            let name = item.ব্যায়ামের_নাম || item.name || 'ব্যায়াম';
            let image = item.ছবি || item.image || '';
            if (item.কিভাবে_করবেন && item.কিভাবে_করবেন.title_image) {
                image = item.কিভাবে_করবেন.title_image;
            }
            let reasons = item.বিস্তারিত_কারণ || item.detailed_reasons || [];
            let videoLink = item.কিভাবে_করবেন?.video_link || '';
            
            categorizedItems['🏃‍♀️ ব্যায়াম'].push({
                নাম: name,
                ছবি: image,
                বিস্তারিত_কারণ: reasons,
                ভিডিও_লিঙ্ক: videoLink
            });
        });
    } 
    else if (type === 'caution') {
        categorizedItems['⚠️ সতর্কতা'] = jsonData['সতর্কতা'] || [];
    }

    // ডাটা রেন্ডার করা
    if(Object.keys(categorizedItems).length > 0){
        Object.keys(categorizedItems).forEach(categoryName => {
            const items = categorizedItems[categoryName];
            
            if (items.length > 0) {
                const categoryTitle = document.createElement('h2');
                categoryTitle.className = 'category-title';
                categoryTitle.innerText = categoryName;
                displayArea.appendChild(categoryTitle);
                
                items.forEach(item => {
                    let card = document.createElement('div');
                    card.className = "card";
                    
                    let content = "";
                    
                    if (type === 'caution') {
                        content = `<div class="caution-item">⚠️ ${item}</div>`;
                    } 
                    else {
                        content += `<h3>${item.নাম}</h3>`;
                        
                        if(item.ছবি){
                            content += `<img src="${item.ছবি}" alt="${item.নাম}" onerror="this.style.display='none'">`;
                        }
                        
                        if(item.বিস্তারিত_কারণ && item.বিস্তারিত_কারণ.length > 0){
                            content += `<ul>`;
                            item.বিস্তারিত_কারণ.forEach(desc => {
                                content += `<li>${desc}</li>`;
                            });
                            content += `</ul>`;
                        }
                        
                        if(item.ভিডিও_লিঙ্ক){
                            content += `<a href="${item.ভিডিও_লিঙ্ক}" target="_blank" class="video-btn">▶ ভিডিও টিউটোরিয়াল</a>`;
                        }
                    }
                    
                    card.innerHTML = content;
                    displayArea.appendChild(card);
                });
            }
        });
    } else {
        displayArea.innerHTML = "<p style='text-align:center; padding:40px; color:#666; font-size:18px;'>😔 দুঃখিত, কোনো তথ্য পাওয়া যায়নি!</p>";
    }
}

// ৫. ব্যাক বাটন
function goStepBack(){
    const homeScreen = document.getElementById('home-screen');
    const optionScreen = document.getElementById('option-screen');
    const dataScreen = document.getElementById('data-screen');
    const backBtn = document.getElementById('back-btn');

    if (dataScreen.style.display === 'block') {
        dataScreen.style.display = 'none';
        optionScreen.style.display = 'block';
    }
    else if (optionScreen.style.display === 'block') {
        optionScreen.style.display = 'none';
        homeScreen.style.display = 'block';
        backBtn.style.display = 'none';
    }
}

// ৬. ড্যাশবোর্ড
function goDashboard(){
    window.location.href = "dashboard.html";
}