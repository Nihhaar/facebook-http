/* This js file assumes that you link it at the end of the html file */
setupDate();

function genOption(arg1){
    var anchor = document.createElement("button");
    anchor.classList.add("dropdown-item");
    anchor.href = "";
    anchor.textContent = arg1;
    return anchor;
}

function setupDate(){
    $("#day").empty();
    $("#month").empty();
    $("#year").empty();

    /* Setup day */
    for(var i=1; i<32; i++)
      document.getElementById("day").appendChild(genOption(i));

    /* Setup month */
    $("#month").append($(genOption('Jan')));
    $("#month").append($(genOption('Feb')));
    $("#month").append($(genOption('Mar')));
    $("#month").append($(genOption('Apr')));
    $("#month").append($(genOption('May')));
    $("#month").append($(genOption('Jun')));
    $("#month").append($(genOption('Jul')));
    $("#month").append($(genOption('Aug')));
    $("#month").append($(genOption('Sep')));
    $("#month").append($(genOption('Oct')));
    $("#month").append($(genOption('Nov')));
    $("#month").append($(genOption('Dec')));

    /* Setup year */
    for(var i=1905; i<2018; i++)
      document.getElementById("year").appendChild(genOption(i));
}
