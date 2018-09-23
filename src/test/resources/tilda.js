<style>
.t778__bgimg {
    width: 300px;
    margin: auto;
    -webkit-transition:  1s ease;
    -o-transition:  1s ease;
    -moz-transition:  1s ease;
    transition:  1s ease;
}

.t778__bgimg:hover {
    -webkit-transform: scale(1.1);
    transform: scale(1.1);
    opacity:0.6;
}

.t778__textwrapper t778__paddingsmall {
    padding-top:0px;
}

</style>

<script type="text/javascript">

/* jshint jquery: true */

var transitianCssEffect = { "transition" : "background 0.5s ease" };

var setsBlock;
var nanoGreySetBlock;
var patrolSetBlock;

var nanoGreyAccessorySelect;
var nanoGreyDetailsButton;
var nanoGreyOldPricePopUp;

var patrolColorSelect;
var patrolAccessorySelect;
var patrolDetailsButton;
var patrolOldPricePopUp;

$(document).ready(function(){
    setsBlock = $('#rec66671186');
    nanoGreySetBlock = setsBlock.find($('div[data-product-lid="1521897288438"]'));
    patrolSetBlock = setsBlock.find($('div[data-product-lid="1536498211622"]'));

    fixNanoGreyBlock();
    fixPatrolBlock();
});

function fixNanoGreyBlock() {
    nanoGreyAccessorySelect = nanoGreySetBlock.find($('select.t-product__option-select'));
    nanoGreyDetailsButton = nanoGreySetBlock.find($('a[href="#prodpopup"]'));

    nanoGreyOldPricePopUp = nanoGreySetBlock.find($('div[field="li_price_old__1521897288438"]')).last();

    nanoGreyAccessorySelect.change( changeNanoGreySelectHandler );
    nanoGreyDetailsButton.each(function (idx) {
        $(this).click( changeNanoGreySelectHandler );
        $(this).click( function () {reachYMetrikaGoal("OpenNanoGreySetPopUp");} );
    });
}

var changeNanoGreySelectHandler = function () {
    console.log('changeNanoGreySelectHandler');
    var selectedAccessory = nanoGreyAccessorySelect.find($('select.t-product__option-select option:selected'));
    var selectedAccessoryText = selectedAccessory.text();


    var imgUrl;
    var oldPrice;
    if (selectedAccessoryText.indexOf("NS-адаптер") === 0) {
        imgUrl = getNanoGreyImage("NSAD");
        oldPrice = "7400";
    } else if (selectedAccessoryText.indexOf("Barista Kit+") > -1) {
        imgUrl = getNanoGreyImage("ALL");
        oldPrice = "9600";
    } else {
        imgUrl = getNanoGreyImage("NPBK");
        oldPrice = "8300";
    }

    nanoGreySetBlock.find($('div.t-slds__bgimg')).each(function (idx) {
        $(this).attr("data-original", imgUrl);
        $(this).css({ "background-image" : to_background_image_prop(imgUrl) });
        $(this).css(transitianCssEffect);

        $(this).parent().attr("data-img-zoom-url", imgUrl);
    });

    nanoGreyOldPricePopUp.text(oldPrice);
};

function getNanoGreyImage(articular) {
    switch (articular) {
        case "NSAD" :
            return "https://static.tildacdn.com/tild6535-3164-4863-b935-656232353462/NPCS_Set_NSAD.jpg";

        default:
        case "ALL" :
            return "https://static.tildacdn.com/tild6536-3962-4337-b036-393565646564/NPCS_Set_All.jpg";

        case "NPBK" :
            return "https://static.tildacdn.com/tild3537-3830-4134-a233-633663346536/NPCS_Set_NPBK.jpg";
    }
}

// PATROL

function fixPatrolBlock() {
    patrolColorSelect = patrolSetBlock.find($('select.t-product__option-select')).eq(0);
    patrolAccessorySelect = patrolSetBlock.find($('select.t-product__option-select')).eq(1);
    patrolDetailsButton = patrolSetBlock.find($('a[href="#prodpopup"]'));

    patrolOldPricePopUp = patrolSetBlock.find($('div[field="li_price_old__1536498211622"]')).last();

    patrolColorSelect.change( changePatrolSelectHandler );
    patrolAccessorySelect.change( changePatrolSelectHandler );
    patrolDetailsButton.each(function (idx) {
        $(this).click( changePatrolSelectHandler );
        $(this).click( function () {reachYMetrikaGoal("OpenNanoPatrolSetPopUp");} );
    });
}

var changePatrolSelectHandler = function () {
    console.log('changePatrolSelectHandler');
    var selectedColor = patrolColorSelect.find($('select.t-product__option-select option:selected'));
    var selectedAccessory = patrolAccessorySelect.find($('select.t-product__option-select option:selected'));

    var selectedColorText = selectedColor.text();
    var selectedAccessoryText = selectedAccessory.text();


    var imgUrl;
    var oldPrice;
    if (selectedAccessoryText.indexOf("NS-адаптер") === 0) {
        imgUrl = getPatrolImage(selectedColorText, "NSAD");
        oldPrice = "6500";
    } else if (selectedAccessoryText.indexOf("Barista Kit+") > -1) {
        imgUrl = getPatrolImage(selectedColorText, "ALL");
        oldPrice = "8700";
    } else {
        imgUrl = getPatrolImage(selectedColorText, "NPBK");
        oldPrice = "7400";
    }

    patrolSetBlock.find($('div.t-slds__bgimg')).each(function (idx) {
        $(this).attr("data-original", imgUrl);
        $(this).css({ "background-image" : to_background_image_prop(imgUrl) });
        $(this).css(transitianCssEffect);

        $(this).parent().attr("data-img-zoom-url", imgUrl);
    });

    patrolOldPricePopUp.text(oldPrice);
};

function getPatrolImage(color, articular) {
    switch (color) {
        default:
        case "Оранжевый":
            switch (articular) {
                case "NSAD" :
                    return "https://static.tildacdn.com/tild3034-6236-4830-b965-316231316336/NPPO_Set_NSAD.jpg";

                default:
                case "ALL" :
                    return "https://static.tildacdn.com/tild6161-3838-4137-b330-353033303634/NPPO_Set_All.jpg";

                case "NPBK" :
                    return "https://static.tildacdn.com/tild6635-3830-4963-b863-623635346161/NPPO_Set_NPBK.jpg";
            }
        case "Желтый":
            switch (articular) {
                case "NSAD" :
                    return "https://static.tildacdn.com/tild3237-6362-4730-b764-366633393030/NPPY_Set_NSAD.jpg";

                default:
                case "ALL" :
                    return "https://static.tildacdn.com/tild3835-6166-4633-b566-636637313861/NPPY_Set_All.jpg";

                case "NPBK" :
                    return "https://static.tildacdn.com/tild3066-3735-4361-a137-333338653665/NPPY_Set_NPBK.jpg";
            }
        case "Красный":
            switch (articular) {
                case "NSAD" :
                    return "https://static.tildacdn.com/tild3066-3765-4266-b262-663133396235/NPPR_Set_NSAD.jpg";

                default:
                case "ALL" :
                    return "https://static.tildacdn.com/tild3139-6532-4137-a466-353966323933/NPPR_Set_All.jpg";

                case "NPBK" :
                    return "https://static.tildacdn.com/tild3462-6662-4237-a530-623530326139/NPPR_Set_NPBK.jpg";
            }
    }
}

function reachYMetrikaGoal(goalName) {
    console.log("Goal: " + goalName);

    console.log("send to yaCounter44688592");
    if (yaCounter44688592 !== undefined) {
        yaCounter44688592.reachGoal(goalName);
    } else {
        console.log("yaCounter44688592 is undefined")
    }
}

function to_background_image_prop(imgUrl) {
    return "url(\"" + imgUrl + "\")";
}

</script>
