// Tabs handler (ma.html + baby.html)
(function () {
  const tabButtons = document.querySelectorAll(".tab-btn");
  if (!tabButtons.length) return;

  tabButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      const group = btn.closest(".pro-tabs, .tabs");
      const allBtns = group ? group.querySelectorAll(".tab-btn") : tabButtons;

      // deactivate all
      allBtns.forEach(b => {
        b.classList.remove("active");
        b.setAttribute("aria-selected", "false");
      });

      // activate clicked
      btn.classList.add("active");
      btn.setAttribute("aria-selected", "true");

      // find container card and switch contents inside it
      const container = document.querySelector(".content-card") || document; 
      const allContents = container.querySelectorAll(".tab-content");

      allContents.forEach(c => c.classList.remove("active"));

      const targetId = btn.getAttribute("data-tab");
      const targetEl = document.getElementById(targetId);
      if (targetEl) targetEl.classList.add("active");
    });
  });
})(); 

