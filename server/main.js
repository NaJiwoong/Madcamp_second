/*
  There are collections: Users, Address, Pictures
*/

var express = require('express');
var bodyParser = require('body-parser');
var app = express();
var fs = require('fs');
var path = require('path');
var qs = require('querystring');
var sanitizeHtml = require('sanitize-html');
var template = require('./lib/template.js');
 
// For mongoDB
const mongo = require('mongodb');
const url = "mongodb+srv://server:madcamp@cluster0-cgg1u.mongodb.net/test?retryWrites=true&w=majority";

function addToDB(myobj, collections){
  mongo.connect(url, {useNewUrlParser: true}, (err, db) => {
    if(err) {
      console.log(err);
      process.exit(0);
    }
    var dbo = db.db('Madcamp');
    // var myobj = { id: "1", name: "americano" };
    console.log(myobj);
    dbo.collection(collections).insertOne(myobj, function(err, res) {
        if (err) throw err;
        console.log("1 info inserted");
        db.close();
    });
  });
}

// Get DB, you should make query as 'var query = {field: "type"}'
function findDB(query, collections){
  mongo.connect(url, {useNewUrlParser: true}, (err, db) => {
    if(err) {
      console.log(err);
      return;
    }
    var dbo = db.db('Madcamp');
    dbo.collection(collections).find(query).toArray(function(err, result){
      if (err) throw err;
      console.log(result);
      db.close();
    });
  });
}

function removeDB(myobj, collections){
  mongo.connect(url, {useNewUrlParser: true}, (err, db) => {
    if(err) {
      console.log(err);
      return;
    }
    var dbo = db.db('Madcamp');
    console.log(myobj);
    // var deletion = dbo.collection.find(myobj).toArray
    dbo.collection(collections).deleteOne(myobj, function(err, result){
      if (err) throw err;
      console.log("Deleted successfully");
      db.close();
    });
  });
}


app.use(bodyParser.urlencoded({limit:"500mb", extended:false}));
app.use(bodyParser.json({limit: "200mb"}));

//route, routing
//app.get('/', (req, res) => res.send('Hello World!'))
app.get('/', function(request, response) { 
  fs.readdir('./data', function(error, filelist){
    var title = 'Welcome';
    var description = 'Hello, Node.js';
    var list = template.list(filelist);
    var html = template.HTML(title, list,
      `<h2>${title}</h2>${description}`,
      `<a href="/create">create</a>`
    ); 
    response.send(html);
  });
});


/* Login Process */

app.post('/login', function(req, res){
  console.log("login request!");

  var id = req.body.id;
  var pass = req.body.password;
  var query = {id: id};
  var success = "failure";
  console.log(query);
  /**/ 
  mongo.connect(url, {useNewUrlParser: true}, (err, db) => {
    if(err) {
      console.log(err);
      return;
    }
    var dbo = db.db('Madcamp');
    var checkid = "";
    var checkpass ="";
    dbo.collection("Users").find(query).toArray(function(err, result){
      if (err) throw err;
      if (!result.length){
        console.log("cannot find id");
        success = "failure";
      }else{
        // console.log(result);
        checkid = result[0].id;
        checkpass = result[0].password;
        db.close();
      }

      if (checkid == id && checkpass == pass){
        success = "success";
      }else{
        success = "failure";
      }
      inputquery = {id: id, password: pass};
      console.log(inputquery);
      res.write(success);
      console.log("sent success info");
      res.end();
    });
    db.close();
  });
});



/* Registeration Process */

app.post('/register', function(req, res){
  console.log("register request!");

  var id = req.body.id;
  var pass = req.body.password;
  var Registeration = {id: id, password: pass};
  var query = {id: id};
  console.log('Register: ' + Registeration);
  
  /* Check existence and upload */
  mongo.connect(url, {useNewUrlParser: true}, (err, db) => {
    if(err) {
      console.log(err);
      process.exit(0);
    }
    var dbo = db.db('Madcamp');
    // var myobj = { id: "1", name: "americano" };
 
    dbo.collection("Users").find(query).toArray(function(err, result){
      if (err) throw err;
      if (!result.length){
        success = "success";
      }else{
        // console.log(result);
        checkid = result[0].id;
        success = "failure";
        db.close();
      }

      res.write(success);
      res.end();
      console.log("sent success info");
      if(success == "success"){
        dbo.collection("Users").insertOne(Registeration, function(err, ress) {
          if (err) throw err;
          console.log("1 info inserted");
          db.close();
        });
      }
    });
      ////////////////////////////////////
    
  });
  //////////////////////////////////////////////

});


/* Withdrawal Process */
app.post('/withdraw', function(req, res){
  console.log("withdraw request!");

  var id = req.body.id;
  var pass = req.body.password;
  var query = {id: id};
  
  removeDB(query, "Users");
  removeDB(query, "Cloepatra");

  res.write("success");
  res.end();

});


/* Address uploading process */
app.post('/address', function(req, res){
  console.log("Address upload request!");

  var id = req.body.id;
  var pass = req.body.password;
  var address = req.body.address;

  query = {id: id};
  myobj = {id: id, address: address};
  collections = "Address";
  console.log(myobj);

  mongo.connect(url, {useNewUrlParser: true}, (err, db) => {
    if(err) {
      console.log(err);
      process.exit(0);
    }
    var dbo = db.db('Madcamp');
    // var myobj = { id: "1", name: "americano" };
    dbo.collection(collections).deleteOne(query, function(err, result1){
      if (err) throw err;

      dbo.collection(collections).insertOne(myobj, function(err, result) {
        if (err){
         res.write("failure");
         res.end();
         throw err;
        }
        console.log("1 info inserted");
        db.close();
        res.write("success");
        console.log("Sent success info: Address upload finished");
        res.end();
      });
    });
  });
});

/* gallery uploading process */
app.post('/gallery', function(req, res){
  console.log("Gallery upload request!");

  var id = req.body.id;
  var pass = req.body.password;
  var gallery = req.body.gallery;

  query = {id: id};
  myobj = {id: id, gallery: gallery};
  collections = "Pictures";

  mongo.connect(url, {useNewUrlParser: true}, (err, db) => {
    if(err) {
      console.log(err);
      process.exit(0);
    }
    var dbo = db.db('Madcamp');
    // var myobj = { id: "1", name: "americano" };
    dbo.collection(collections).deleteOne(query, function(err, result1){
      if (err){ 
        res.write("failure");
        res.end();
        throw err;
      }
      dbo.collection(collections).insertOne(myobj, function(err, result) {
        if (err){
         res.write("failure");
         res.end();
         throw err;
        }
        console.log("1 info inserted");
        db.close();
        res.write("success");
        console.log("Sent success info: Gallery upload finished");
        res.end();
      });
    });     
  });
});

//////////////////////////////////////////Cleopatra///////////////////////////












///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/******************************************** Above is for Android client ***********************************************/
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 
app.get('/page/:pageId', function(request, response) { 
  fs.readdir('./data', function(error, filelist){
    var filteredId = path.parse(request.params.pageId).base;
    fs.readFile(`data/${filteredId}`, 'utf8', function(err, description){
      var title = request.params.pageId;
      var sanitizedTitle = sanitizeHtml(title);
      var sanitizedDescription = sanitizeHtml(description, {
        allowedTags:['h1']
      });
      var list = template.list(filelist);
      var html = template.HTML(sanitizedTitle, list,
        `<h2>${sanitizedTitle}</h2>${sanitizedDescription}`,
        ` <a href="/create">create</a>
          <a href="/update/${sanitizedTitle}">update</a>
          <form action="/delete_process" method="post">
            <input type="hidden" name="id" value="${sanitizedTitle}">
            <input type="hidden" name="desc" value="${sanitizedDescription}">
            <input type="submit" value="delete">
          </form>`
      );
      response.send(html);
    });
  });
});
 
app.get('/create', function(request, response){
  fs.readdir('./data', function(error, filelist){
    var title = 'WEB - create';
    var list = template.list(filelist);
    var html = template.HTML(title, list, `
      <form action="/create_process" method="post">
        <p><input type="text" name="title" placeholder="title"></p>
        <p>
          <textarea name="description" placeholder="description"></textarea>
        </p>
        <p>
          <input type="submit">
        </p>
      </form>
    `, '');
    response.send(html);
  });
});
 
app.post('/create_process', function(request, response){
  var post = request.body;
  var title = post.title;
  var description = post.description;
  var myobj = { id: title, name: description };
  addToDB(myobj, "Address");
  fs.writeFile(`data/${title}`, description, 'utf8', function(err){
    response.redirect(`/page/${title}`);
  });
});
 
app.get('/update/:pageId', function(request, response){
  var curtitle = request.params.pageId;
  fs.readdir('./data', function(error, filelist){
    var filteredId = path.parse(request.params.pageId).base;
    fs.readFile(`data/${filteredId}`, 'utf8', function(err, description){
      var title = request.params.pageId;
      var list = template.list(filelist);
      var html = template.HTML(title, list,
        `
        <form action="/update_process" method="post">
          <input type="hidden" name="curid" value="${curtitle}">
          <input type="hidden" name="id" value="${title}">
          <p><input type="text" name="title" placeholder="title" value="${title}"></p>
          <p>
            <textarea name="description" placeholder="description">${description}</textarea>
          </p>
          <p>
            <input type="submit">
          </p>
        </form>
        `,
        `<a href="/create">create</a> <a href="/update/${title}">update</a>`
      );
      response.send(html);
    });
  });
});
 
app.post('/update_process', function(request, response){
  var post = request.body;
  var curobj = { id: post.curid};
  removeDB(curobj, "Address");
  var id = post.id;
  var title = post.title;
  var description = post.description;
  var myobj = { id: title, name: description};
  addToDB(myobj, "Address");
  fs.rename(`data/${id}`, `data/${title}`, function(error){
    fs.writeFile(`data/${title}`, description, 'utf8', function(err){
      response.redirect(`/page/${title}`);
    })
  });
});
 
app.post('/delete_process', function(request, response){
  var post = request.body;
  var id = post.id;
  var myobj = { id: id, name: post.desc };
  removeDB(myobj, "Address");
  var filteredId = path.parse(id).base;
  fs.unlink(`data/${filteredId}`, function(error){
    response.redirect('/');
  });
});
 
app.listen(3000, function() {
  console.log('Example app listening on port 3000!')
});

/*
var http = require('http');
var fs = require('fs');
var url = require('url');
var qs = require('querystring');
var template = require('./lib/template.js');
var path = require('path');
var sanitizeHtml = require('sanitize-html');

var app = http.createServer(function(request,response){
    var _url = request.url;
    var queryData = url.parse(_url, true).query;
    var pathname = url.parse(_url, true).pathname;
    if(pathname === '/'){
      if(queryData.id === undefined){
        fs.readdir('./data', function(error, filelist){
          var title = 'Welcome';
          var description = 'Hello, Node.js';
          var list = template.list(filelist);
          var html = template.HTML(title, list,
            `<h2>${title}</h2>${description}`,
            `<a href="/create">create</a>`
          );
          response.writeHead(200);
          response.end(html);
        });
      } else {
        fs.readdir('./data', function(error, filelist){
          var filteredId = path.parse(queryData.id).base;
          fs.readFile(`data/${filteredId}`, 'utf8', function(err, description){
            var title = queryData.id;
            var sanitizedTitle = sanitizeHtml(title);
            var sanitizedDescription = sanitizeHtml(description, {
              allowedTags:['h1']
            });
            var list = template.list(filelist);
            var html = template.HTML(sanitizedTitle, list,
              `<h2>${sanitizedTitle}</h2>${sanitizedDescription}`,
              ` <a href="/create">create</a>
                <a href="/update?id=${sanitizedTitle}">update</a>
                <form action="delete_process" method="post">
                  <input type="hidden" name="id" value="${sanitizedTitle}">
                  <input type="submit" value="delete">
                </form>`
            );
            response.writeHead(200);
            response.end(html);
          });
        });
      }
    } else if(pathname === '/create'){
      fs.readdir('./data', function(error, filelist){
        var title = 'WEB - create';
        var list = template.list(filelist);
        var html = template.HTML(title, list, `
          <form action="/create_process" method="post">
            <p><input type="text" name="title" placeholder="title"></p>
            <p>
              <textarea name="description" placeholder="description"></textarea>
            </p>
            <p>
              <input type="submit">
            </p>
          </form>
        `, '');
        response.writeHead(200);
        response.end(html);
      });
    } else if(pathname === '/create_process'){
      var body = '';
      request.on('data', function(data){
          body = body + data;
      });
      request.on('end', function(){
          var post = qs.parse(body);
          var title = post.title;
          var description = post.description;
          fs.writeFile(`data/${title}`, description, 'utf8', function(err){
            response.writeHead(302, {Location: `/?id=${title}`});
            response.end();
          })
      });
    } else if(pathname === '/update'){
      
    } else if(pathname === '/update_process'){
      var body = '';
      request.on('data', function(data){
          body = body + data;
      });
      request.on('end', function(){
          var post = qs.parse(body);
          var id = post.id;
          var title = post.title;
          var description = post.description;
          fs.rename(`data/${id}`, `data/${title}`, function(error){
            fs.writeFile(`data/${title}`, description, 'utf8', function(err){
              response.writeHead(302, {Location: `/?id=${title}`});
              response.end();
            })
          });
      });
    } else if(pathname === '/delete_process'){
      var body = '';
      request.on('data', function(data){
          body = body + data;
      });
      request.on('end', function(){
          var post = qs.parse(body);
          var id = post.id;
          var filteredId = path.parse(id).base;
          fs.unlink(`data/${filteredId}`, function(error){
            response.writeHead(302, {Location: `/`});
            response.end();
          })
      });
    } else {
      response.writeHead(404);
      response.end('Not found');
    }
});
app.listen(3000);*/