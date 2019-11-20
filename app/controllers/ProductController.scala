package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, ControllerComponents}
import play.api.libs.json.Json
import scala.concurrent.{Future, ExecutionContext}
import models.{Product, Description, ProductsInfo}
import repositories.ProductRepository

class ProductController @Inject()(
    components: ControllerComponents,
    ProductRepo: ProductRepository,
)(implicit ec: ExecutionContext) extends AbstractController(components) {


  def listProducts = Action.async { _ =>
    ProductRepo.list.map { products =>
      Ok(Json.toJson(products))
    }
  }

  def listProds = Action.async { _ =>
    ProductRepo.listprod.map { products =>
      Ok(Json.toJson(products)) 
    }
  }

  def createProductDescription = Action.async(parse.json) { request => {
    request.body
      .validate[(Product, Description)]
      .map { case (product, description) => 
        ProductRepo
          .createProductDescription(product, description)
          .map { _ =>
            Created(
              Json.obj(
                "status" -> "success"
              )
            )
          }
          .recoverWith {
            case e =>
              Future {
                InternalServerError(
                  Json.obj(
                    "status" -> "failed",
                    "error" -> e.toString()
                  )
                )
              }
          }
      }
      .getOrElse(
        Future.successful(
          BadRequest(
            Json.obj(
              "status" -> "failed",
              "error" -> "Invalid format"
            )
          )
        )
      )
    }
  }

  def getProduct(id: Int) = Action.async { _ =>
    ProductRepo.getSingleProduct(id).map { result =>
      Ok(Json.toJson(result))
    }
  }

  def getProductByTerm(term: String) = Action.async { _ =>
    ProductRepo.getProductByT(term).map { result =>
      Ok(Json.toJson(result))
    }
  }

  def updateProduct(id: Int) = Action.async(parse.json) { request =>
    request.body
      .validate[Product]
      .map { product =>
        ProductRepo
          .update(id, product)
          .map {
            case e =>
              NotFound(
                Json.obj(
                  "status" -> "failed",
                  "error" -> "Not Found"
                )
              )
            case _ => Ok(Json.obj("status" -> "success"))
          }
          .recoverWith {
            case e =>
              Future {
                InternalServerError(
                  Json.obj(
                    "status" -> "failed",
                    "error" -> e.toString()
                  )
                )
              }
          }
      }
      .getOrElse(
        Future.successful(
          BadRequest(
            Json.obj(
              "status" -> "failed",
              "error" -> "Invalid format"
            )
          )
        )
      )
  }

  def deleteProduct(id: Int) = Action.async { _ =>
    ProductRepo
      .delete(id)
      .map {
        case e =>
          NotFound(
            Json.obj(
              "status" -> "failed",
              "error" -> "Not Found"
            )
          )
        case _ => Ok(Json.obj("status" -> "success"))
      }
      .recoverWith {
        case e =>
          Future {
            InternalServerError(
              Json.obj(
                "status" -> "failed",
                "error" -> e.toString()
              )
            )
          }
      }
  }
 }