<html>
    <head>
        <title>ftp上传文件</title>
    </head>
    <body>
        <form action="/upload" method="POST" ENCTYPE="multipart/form-data">
            <input type="file" name="file">
            <br>
            <input type="submit" value="提交">
        </form>
    </body>
</html>