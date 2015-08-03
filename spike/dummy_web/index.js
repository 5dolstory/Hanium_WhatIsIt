var app = require('express')();
var http = require('http').Server(app);

app.get('/rss/humor', function(req, res){
    res.sendFile(__dirname + '/public/rss_humor_list.xml');
});
app.get('/feed/humor/1', function(req, res){
    res.sendFile(__dirname + '/public/feed_humor_id.html');
});
app.get('/rss/best', function(req, res){
    res.sendFile(__dirname + '/public/rss_humor_list.xml');
});

http.listen(3001, function(){
    console.log('listening on *:3001');
});
