function toHumanSize(fileLength) {
    if(fileLength < 1000) {
        return fileLength + "B";
    } else if(fileLength < 1000 * 1000) {
        return (fileLength / 1000.0).toFixed(1) + "KB";
    } else if(fileLength < 1000 * 1000 * 1000) {
        return (fileLength / 1000.0 / 1000.0).toFixed(1) + "MB";
    } else if(fileLength >= 1000 * 1000 * 1000) {
        return (fileLength / 1000.0 / 1000.0 / 1000.0).toFixed(1) + "GB";
    }
}

///////////////////spinner

function showModalSpinner(){
    var spinner_opts = {
        lines: 11, // The number of lines to draw
        length: 41, // The length of each line
        width : 10, // The line thickness
        radius : 48, // The radius of the inner circle
        corners : 1, // Corner roundness (0..1)
        rotate : 0, // The rotation offset
        direction : 1, // 1: clockwise, -1: counterclockwise
        color : '#fff', // #rgb or #rrggbb or array of colors
        speed : 0.7, // Rounds per second
        trail : 60, // Afterglow percentage
        shadow : false, // Whether to render a shadow
        hwaccel : false, // Whether to use hardware acceleration
        className : 'spinner', // The CSS class to assign to the spinner
        zIndex : 2e9, // The z-index (defaults to 2000000000)
        top : 'auto', // Top position relative to parent in px
        left : 'auto' // Left position relative to parent in px
    };
    var spinObj = $('<div id="spin_modal_overlay" style="background-color: rgba(0, 0, 0, 0.6); width:100%; height:100%; position:fixed; top:0px; left:0px; z-index:10000"/>');
    $('body').append(spinObj);
    var spinner = new Spinner(spinner_opts).spin(spinObj[0]);
}

function hideModalSpinner(){
    if($('#spin_modal_overlay')){
        $('#spin_modal_overlay').remove();
    }
}
