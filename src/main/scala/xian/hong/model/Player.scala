package xian.hong.model
import scalafx.beans.property.{StringProperty, ObjectProperty}
import xian.hong.util.Database
import scalikejdbc._
import scala.util.{ Try, Success, Failure }
import scalafx.collections.ObservableBuffer

class Player (val playerName: StringProperty, val score: ObjectProperty[Int]) extends Database {
	val id = ObjectProperty[Long](-1)

	//inserts a new record if player id is -1, updates the record if the player exists
	def save(): Try[Long] = {
		if (!(isExist)) {
			Try(DB autoCommit { implicit session => 
				id.value = sql"""insert into player (playerName, score) values (${playerName.value}, ${score.value})""".updateAndReturnGeneratedKey().apply()
				id.value
			})
		} else {
			Try(DB autoCommit { implicit session => 
				sql"""
				update player 
				set 
				playerName  = ${playerName.value} ,
				score   = ${score.value}
				where id = ${id.value}
				""".update.apply()
			})
		}		
	}
	
	//check if the player exists
	def isExist: Boolean =  {
		DB readOnly { implicit session =>
			sql"""
				select * from player where 
				id = ${id.value}
			""".map(rs => rs.string("playerName")).single.apply()
		} match {
			case Some(x) => true
			case None => false
		}
	}
}

object Player extends Database{
    //The data as an observable buffer of Persons.

  	var playerData = new ObservableBuffer[Player]()

	//create player object using apply method
	def apply(_id: Int, _playerName: String, _score: Int): Player = {
		new Player(new StringProperty(_playerName), ObjectProperty[Int](_score)){
			id.value = _id
		}
	}

	//initialize the table if it has not
	def initializeTable() = {
		DB autoCommit { implicit session => 
			sql"""
			create table player (
				id int not null GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
				playerName varchar(64),
				score int
			)
			""".execute.apply()
		}
	}

	//return list of all players
	def allPlayers: List[Player] = {
		DB readOnly { implicit session =>
			sql"select * from player".map(rs => Player(rs.int("id"), rs.string("playerName"), rs.int("score"))).list.apply()
		}
	}
}