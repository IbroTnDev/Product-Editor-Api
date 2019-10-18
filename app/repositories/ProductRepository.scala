package repositories

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future}
import slick.jdbc.PostgresProfile.api._
import models.{Product, Description, ProductsInfo, DescriptionDTO, ProductDTO}

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


/*     def list: Future[Seq[ProductsInfo]] = {
        val query = Products
        .joinLeft(ProductDescriptions).on(_.id === _.description_id)

        db.run(query.result).map { a =>
            a.groupBy(_._1).map { case (_, tuples) =>
            val ((product), _) = tuples.head
            val ProductDescriptions = tuples.flatMap(_._2)
            ProductsInfo(product, ProductDescriptions)
            }.toSeq
    }
    } */

     def list: Future[Seq[ProductsInfo]] = {
        val q = Products
        .joinLeft(ProductDescriptions).on(_.id === _.description_id)
    
        val sortedQuery = q.sortBy(_._1.id)

        db.run(sortedQuery.result).map { results =>
        results.groupBy(_._1).map {
          case (prod, grp) => ProductsInfo(prod, grp.flatMap(_._2))
        }.toSeq
        }
    
    }
    
    def listprod: Future[Seq[Product]] = db.run(Products.result)
    //    def list: Future[Seq[Product]] = db.run(Products.sortBy(id).result)


    def createProductDescription(product: ProductDTO, description: DescriptionDTO) = {
        db.run(Products += Product.fromDTO(product)).map { _ =>
            ()
        }
        createDescription(description);
    }

    def createDescription(description: DescriptionDTO) = {
        db.run(ProductDescriptions += Description.fromDTO(description)).map { _ =>
            ()
        }
    }

    //def getSingleProduct(id: Int) = db.run(Products.filter(_.id === id).result.headOption)

    def getSingleProduct(id: Int): Future[Seq[ProductsInfo]] = {
        val q = Products.filter(i => i.id === id)
        .joinLeft(ProductDescriptions).on(_.id === _.description_id)
    
        val sortedQuery = q.sortBy(_._1.id)

        db.run(sortedQuery.result).map { results =>
        results.groupBy(_._1).map {
          case (prod, grp) => ProductsInfo(prod, grp.flatMap(_._2))
        }.toSeq
        }
    }

    def getProductByT(term: String) = {
          val query = for {
            product <- Products if product.name like s"%$term%" 
          } yield (product.name, product.id)
        db.run(query.result).map{ results => results}
        
    }



    def update(id: Int, product: ProductDTO) = {
      db.run(
        Products.filter(i => i.id === id).update(Product.fromDTO(product))
      )
    }
  
    def delete(id: Int) = {
        db.run(ProductDescriptions.filter(i => i.description_id === id).delete)
        db.run(Products.filter(i => i.id === id).delete)
    }
  
}