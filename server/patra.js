var express = require('express')
var app = express(); 
var socketio = require('socket.io');

var server = app.listen(3001,()=>{
    console.log('Listening at port number 3001') 
})


// For mongoDB
const mongo = require('mongodb');
const url = "mongodb+srv://server:madcamp@cluster0-cgg1u.mongodb.net/test?retryWrites=true&w=majority";

// MongoDB에 user정보 저장
// Collection: Cloepatra : {id: id, victory: victoryNUM, defeat: defeatNUM, highscore: highscore}
// Collection: patraRoom : {name: roomName}

app.get('/getRoom', function(req, res){
  console.log("patraList request!");

  mongo.connect(url, {useNewUrlParser: true}, (err, db) => {
    if(err){
        console.log(err);
        res.write("Failed to Connect to DB");
        res.end();
        return;
    }
    var dbo = db.db('Madcamp');
    dbo.collection("patraRoom").find({}).toArray(function(err, result){
      if (err){
          console.log(err);
          res.write("Failed to load room info");
          res.end();
          db.close();
          return;
      }
      if (!result.length){
          console.log("sent empty");
          res.write("empty");
          res.end();
          db.close();
          return;
      }else{
          array1 = result.map(jsn => JSON.stringify(jsn));
          resultString = array1.toString();
          resultString = "[" + resultString + "]";
          console.log(resultString);
          res.write(resultString);
          console.log("sent result");
        //   res.write("empty");
          res.end();
          db.close();
          return;
      } 
    });
  });
});


/* Room creation Process */

app.get('/createroom/:name', function(req, res){
    console.log("Create room request!");

    var params = req.params.name.split('/');
  
    var query = {name: params[0]};
    
    /* Check existence and upload */
    mongo.connect(url, {useNewUrlParser: true}, (err, db) => {
      if(err) {
        console.log(err);
      }
      var dbo = db.db('Madcamp');

      dbo.collection("patraRoom").find(query).toArray(function(err, result){
        if (err) throw err;
        if (!result.length){
          success = "success";
        }else{
          // console.log(result);
          checkid = result[0].id;
          success = "failure";
          db.close();
          res.write("failure");
          return;
        }
        res.write(success);
        res.end();
        console.log("sent success info");
        if(success == "success"){
          dbo.collection("patraRoom").insertOne(query, function(err, ress) {
            if (err) throw err;
            console.log("1 info inserted");
            db.close();
          });
        }
      });
    });
});


// Get User Information of Cleopatra game
app.get('/getinfo/:id', function(req, res){
    console.log("patra information request!");

    query = {id: req.params.id};

    mongo.connect(url, {useNewUrlParser: true}, (err, db) => {
        if(err){
            console.log(err);
            res.write("Failed to Connect to DB");
            res.end();
            return;
        }
        var dbo = db.db('Madcamp');
        dbo.collection("Cleopatra").find({query}).toArray(function(err, result){
        if (err){
            console.log(err);
            res.write("Failed to load room info");
            res.end();
            db.close();
            return;
        }
        if (!result.length){
            res.write("empty");
            res.end();
            db.close();
            return;
        }else{
            res.write(JSON.stringify(result[0]));
            res.end();
            db.close();
            return;
        } 
        });
    });
});


// Pushin user info of Cleopatra game
app.post('/pushinfo', function(req, res){
    console.log("Pushing information request!");

    var id = req.body.id;
    var vict = req.body.victory;
    var defeat = req.body.defeat;
    var high = req.body.highscore;
    var userinfo = {id: id, victory: vict, defeat: defeat, highscore: high};
    var query = {id: id};

    /* Check existence and upload */
    mongo.connect(url, {useNewUrlParser: true}, (err, db) => {
        if(err) {
        console.log(err);
        process.exit(0);
        }
        var dbo = db.db('Madcamp');
        // var myobj = { id: "1", name: "americano" };

        dbo.collection("Cleopatra").find(query).toArray(function(err, result){
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
            dbo.collection("Cleopatra").insertOne(userinfo, function(err, ress) {
            if (err) throw err;
            console.log("1 info inserted");
            db.close();
            });
        }
        });
    });
});
  

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



//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////     Below is for Sockect Communication   /////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////



//return socket.io server.
var io = socketio.listen(server) // 이 과정을 통해 우리의 express 서버를 socket io 서버로 업그레이드를 시켜줍니다.

//이 배열은 누가 chatroom에 있는지를 보여줍니다.
var whoIsOn= new Map([]);

//이 서버에서는 어떤 클라이언트가 connection event를 발생시키는 것인지 듣고 있습니다.
// callback 으로 넘겨지는 socket에는 현재 클라이언트와 연결되어있는 socket 관련 정보들이 다 들어있습니다.
io.on('connection',function (socket){

    console.log('Socket connected with client!!');
   
    var nickname = ``
   
    //일단 socket.on('login') 이라는 것은 클라이언트가 login 이라는 이벤트를 발생시키면
    //어떤 콜백 함수를 작동시킬 것인지 설정하는 것입니다.
    socket.on('enter',function(data1){
        ////////////////여기서 data에 방 이름을 같이 넘겨주면 서버에서 socket.join 으로 방으로 묶어주는게 좋을듯.
        dataparse = data1.split('/');
        data = dataparse[0];
        room = dataparse[1];
        console.log(`User ${data} entered the room ${room}`);

        socket.join(room);
        
        //user control
        io.to(room).emit('newUser', "New user entered");
        var curRoom = whoIsOn.get(room) //
        if (curRoom == undefined){
            curRoom = [data];
        }else{
            curRoom = undefined;
            myobj = {name: room};
            removeDB(myobj, "patraRoom");
        }
        whoIsOn.set(room, curRoom);

        
        socket.broadcast.to(room).emit("start", "start");           //두 명이 방에 참가하면 방장에게 start 보내면 방장의 프론트에서 녹음을 할 수 있게 된다.
        
        //DB에서 게임 시작한 방 정보 없애기

        // 아래와 같이 하면 그냥 String 으로 넘어가므로 쉽게 파싱을 할 수 있습니다.
        // 그냥 넘기면 JSONArray로 넘어가서 복잡해집니다.
        
        //io.emit 과 socket.emit과 다른 점은 io는 서버에 연결된 모든 소켓에 보내는 것이고
        // socket.emit은 현재 그 소켓에만 보내는 것입니다.       
      
        
    })

    socket.on('say',function(data){
        console.log(`Got data : ${data}`)

        if (data == "-1"){
            socket.broadcast.to(room).emit('victory', data);
        }else{
            socket.emit('myMsg',data)
            // socket.broadcast.emit('newMsg',data) // socket.broadcast.emit은 현재 소켓이외의 서버에 연결된 모든 소켓에 보내는 것.
            socket.broadcast.to(room).emit('newMsg', data);
            socket.broadcast.to(room).emit("start", "start");       //상대방에게만 start를 보내서 button sleep 상태에서 풀어준다. 
        }   
    })


    socket.on('pushinfo', function(data1){
        data = data1.split('/');

        query = {id: data[0]};
        myobj = {id: data[0], victory: data[1], defeat: data[2], highscore: data[3]};

        removeDB(query, "Cleopatra");
        addToDB(myobj, "Cleopatra");

    });




    socket.on('disconn',function(roomname){

        myobj = {name: roomname};

        removeDB(myobj, "patraRoom");
        
        whoIsOn.set(roomname, undefined);

        // myobj = {name: room};
        // removeDB(myobj, "patraRoom");
        
    })

    socket.on('logout',function(room){

        //Delete user in the whoIsOn Arryay
        whoIsOn.splice(whoIsOn.indexOf(nickname),1);
        var data = {
            whoIsOn: whoIsOn,
            disconnected : nickname
        }
        myobj = {name: room};
        removeDB(myobj, "patraRoom");
        whoIsOn.set(roomname, undefined);
        socket.emit('logout',data)
    });


})