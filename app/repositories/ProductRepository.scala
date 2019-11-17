package repositories

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future}
import slick.jdbc.PostgresProfile.api._
import models.{Product, Description, ProductsInfo}

class ProductRepository {
  private val Products = TableQuery[ProductsTable]
  private val ProductDescriptions = TableQuery[DescriptionTable]
  val db = Database.forConfig("database")

  private class ProductsTable(tag: Tag) extends Table[Product](tag, "products") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def category = column[String]("category")
    def code = column[Int]("code")
    def price = column[Double]("price")
    def * =
      (id, name, category, code, price) <> ((Product.apply _).tupled, Product.unapply)
  }

  private class DescriptionTable(tag: Tag) extends Table[Description](tag, "description") {
    def key = column[String]("key")
    def value = column[String]("value")
    def description_id = column[Option[Int]]("description_id")
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def * =
      (key, value, description_id, id) <> ((Description.apply _).tupled, Description.unapply)
      
      def product = foreignKey("constraint_name", description_id, Products)(_.id)
  }

    def list: Future[Seq[ProductsInfo]] = {
        val q = Products
        .joinLeft(ProductDescriptions).on(_.id === _.description_id)
    
        val sortedQuery = q.sortBy(_._1.id)

        db.run(sortedQuery.result).map { results =>
        results.groupBy(_._1).map {
          case (product, description) => ProductsInfo(product.flatMap(_2), description.flatMap(_._2))
        }.toSeq
        }
    
    }
    
    def listprod: Future[Seq[Product]] = db.run(Products.result)
    //    def list: Future[Seq[Product]] = db.run(Products.sortBy(id).result)


    def createProductDescription(product: Product, description: Description) = {
        db.run(Products += Product.fromDTO(product)).map { _ =>
            ()
        }
        createDescription(description);
    } 

    def createDescription(description: Description) = {
        db.run(ProductDescriptions += Description.fromDTO(description)).map { _ =>
            ()
        }
    }

    def getSingleProduct(id: Int): Future[Seq[ProductsInfo]] = {
        val q = Products.filter(_.id === id)
        .joinLeft(ProductDescriptions).on(_.id === _.description_id)
    
        val sortedQuery = q.sortBy(_._1.id)

        db.run(sortedQuery.result).map { results =>
        results.map {
          case (prod, grp) => ProductsInfo(prod, grp.flatMap(_._2))
        }.toSeq
        }
    }

    def getProductByT(term: String): Future[Product] = {
        val query = for {
        product <- Products if product.name like s"%$term%" 
        } yield (product.name, product.id)

        db.run(query.result).map{ results => results}
        
    }



    def update(id: Int, product: Product) = {
      db.run(
        Products.filter(_.id === id).update(Product.fromDTO(product))
      )
    }
  
    def delete(id: Int) = {
        val q = Products.filter(_.id === id)
        .joinLeft(ProductDescriptions).on(_.id === _.description_id)

        db.run(q.result).delete
    }
  
}