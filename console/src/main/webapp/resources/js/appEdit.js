$(function() {
    function handleUpload(formData, i) {
        $.ajax({
            url: '/api/apps/upload',
            processData: false,
            contentType: false,
            data: formData,
            type: 'POST',
            dataType: 'json',
            success: function (result) {
//                console.log(result.name, result.length, result.date);
                $(".app-file-detail" + i + "").show();
                $("#fileInfo" + i).text(result.name + " (" + toHumanSize(result.length) + ")");
                $("#fileDate" + i).text(result.date);
                $("input[name=fileName" + i + "]").val(result.name);
                $("input[name=filePath" + i + "]").val(result.path);
                $("input[name=fileLength" + i + "]").val(result.length);
                $("input[name=fileDate" + i + "]").val(result.date);
                $("input[name=fileChecksum" + i + "]").val(result.checksum);
            },
            error: function (e) {
                $(".app-file-detail" + i + "").hide();
                console.log("upload error = ", e);
                alert("File upload failed :" + e);
            }
        });
    }

    function cancelUpload(i) {
        $("#fileInfo" + i).text("");
        $("#fileDate" + i).text("");
        $("input[name=fileName" + i + "]").val("");
        $("input[name=filePath" + i + "]").val("");
        $("input[name=fileLength" + i + "]").val("");
        $("input[name=fileDate" + i + "]").val("");
        $("input[name=fileChecksum" + i + "]").val("");
        $(".app-file-detail" + i + "").hide();
    }

    //파일선택시 처리.
    $("#appFile1").on("change", function (event) {
        fileList = $(event.delegateTarget)[0].files;
        if (fileList.length == 0) {
            cancelUpload(1);
            return;
        }
        var formData = new FormData();
        formData.append("file", fileList[0]);
        handleUpload(formData, 1);
    });

    $("#appFile2").on("change", function (event) {
        fileList = $(event.delegateTarget)[0].files;
        if (fileList.length == 0) {
            cancelUpload(2);
            return;
        }
        var formData = new FormData();
        formData.append("file", fileList[0]);
        handleUpload(formData, 2);
    });
});