var selectAllChk = document.getElementsByName('selectAll')[0];

selectAllChk.addEventListener("click", function(){
  var elements = document.getElementsByName('checkboxName');
  elements.forEach(el => {
    if (this.checked)
      el.checked = true;
    else
      el.checked = false;
  })
});