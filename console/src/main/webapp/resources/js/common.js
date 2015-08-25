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