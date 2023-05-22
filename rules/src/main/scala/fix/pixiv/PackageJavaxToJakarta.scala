package fix.pixiv

import scala.meta.{Importer, Term}

import scalafix.Patch
import scalafix.v1.{SemanticDocument, SemanticRule}

class PackageJavaxToJakarta extends SemanticRule("PackageJavaxToJakarta") {

  private final val mappings = Map(
    "javax.activation" -> "jakarta.activation",
    "javax.annotation" -> "jakarta.annotation",
    "javax.annotation.security" -> "jakarta.annotation.security",
    "javax.annotation.sql" -> "jakarta.annotation.sql",
    "javax.batch" -> "jakarta.batch",
    "javax.batch.api" -> "jakarta.batch.api",
    "javax.batch.api.chunk" -> "jakarta.batch.api.chunk",
    "javax.batch.api.chunk.listener" -> "jakarta.batch.api.chunk.listener",
    "javax.batch.api.listener" -> "jakarta.batch.api.listener",
    "javax.batch.api.partition" -> "jakarta.batch.api.partition",
    "javax.batch.operations" -> "jakarta.batch.operations",
    "javax.batch.runtime" -> "jakarta.batch.runtime",
    "javax.batch.runtime.context" -> "jakarta.batch.runtime.context",
    "javax.cache" -> "jakarta.cache",
    "javax.cache.annotation" -> "jakarta.cache.annotation",
    "javax.cache.configuration" -> "jakarta.cache.configuration",
    "javax.cache.event" -> "jakarta.cache.event",
    "javax.cache.expiry" -> "jakarta.cache.expiry",
    "javax.cache.integration" -> "jakarta.cache.integration",
    "javax.cache.management" -> "jakarta.cache.management",
    "javax.cache.processor" -> "jakarta.cache.processor",
    "javax.cache.spi" -> "jakarta.cache.spi",
    "javax.decorator" -> "jakarta.decorator",
    "javax.ejb" -> "jakarta.ejb",
    "javax.ejb.embeddable" -> "jakarta.ejb.embeddable",
    "javax.ejb.spi" -> "jakarta.ejb.spi",
    "javax.el" -> "jakarta.el",
    "javax.enterprise" -> "jakarta.enterprise",
    "javax.enterprise.concurrent" -> "jakarta.enterprise.concurrent",
    "javax.enterprise.context" -> "jakarta.enterprise.context",
    "javax.enterprise.context.control" -> "jakarta.enterprise.context.control",
    "javax.enterprise.context.spi" -> "jakarta.enterprise.context.spi",
    "javax.enterprise.deploy" -> "jakarta.enterprise.deploy",
    "javax.enterprise.deploy.model" -> "jakarta.enterprise.deploy.model",
    "javax.enterprise.deploy.model.exceptions" -> "jakarta.enterprise.deploy.model.exceptions",
    "javax.enterprise.deploy.shared" -> "jakarta.enterprise.deploy.shared",
    "javax.enterprise.deploy.shared.factories" -> "jakarta.enterprise.deploy.shared.factories",
    "javax.enterprise.deploy.spi" -> "jakarta.enterprise.deploy.spi",
    "javax.enterprise.deploy.spi.exceptions" -> "jakarta.enterprise.deploy.spi.exceptions",
    "javax.enterprise.deploy.spi.factories" -> "jakarta.enterprise.deploy.spi.factories",
    "javax.enterprise.deploy.spi.status" -> "jakarta.enterprise.deploy.spi.status",
    "javax.enterprise.event" -> "jakarta.enterprise.event",
    "javax.enterprise.inject" -> "jakarta.enterprise.inject",
    "javax.enterprise.inject.literal" -> "jakarta.enterprise.inject.literal",
    "javax.enterprise.inject.se" -> "jakarta.enterprise.inject.se",
    "javax.enterprise.inject.spi" -> "jakarta.enterprise.inject.spi",
    "javax.enterprise.inject.spi.configurator" -> "jakarta.enterprise.inject.spi.configurator",
    "javax.enterprise.util" -> "jakarta.enterprise.util",
    "javax.inject" -> "jakarta.inject",
    "javax.interceptor" -> "jakarta.interceptor",
    "javax.jms" -> "jakarta.jms",
    "javax.json" -> "jakarta.json",
    "javax.json.bind" -> "jakarta.json.bind",
    "javax.json.bind.adapter" -> "jakarta.json.bind.adapter",
    "javax.json.bind.annotation" -> "jakarta.json.bind.annotation",
    "javax.json.bind.config" -> "jakarta.json.bind.config",
    "javax.json.bind.serializer" -> "jakarta.json.bind.serializer",
    "javax.json.bind.spi" -> "jakarta.json.bind.spi",
    "javax.json.spi" -> "jakarta.json.spi",
    "javax.json.stream" -> "jakarta.json.stream",
    "javax.jws" -> "jakarta.jws",
    "javax.jws.soap" -> "jakarta.jws.soap",
    "javax.management" -> "jakarta.management",
    "javax.management.j2ee" -> "jakarta.management.j2ee",
    "javax.management.j2ee.statistics" -> "jakarta.management.j2ee.statistics",
    "javax.persistence" -> "jakarta.persistence",
    "javax.persistence.criteria" -> "jakarta.persistence.criteria",
    "javax.persistence.metamodel" -> "jakarta.persistence.metamodel",
    "javax.persistence.spi" -> "jakarta.persistence.spi",
    "javax.resource" -> "jakarta.resource",
    "javax.resource.cci" -> "jakarta.resource.cci",
    "javax.resource.spi" -> "jakarta.resource.spi",
    "javax.resource.spi.endpoint" -> "jakarta.resource.spi.endpoint",
    "javax.resource.spi.security" -> "jakarta.resource.spi.security",
    "javax.resource.spi.work" -> "jakarta.resource.spi.work",
    "javax.security.auth.message" -> "jakarta.security.auth.message",
    "javax.security.auth.message.callback" -> "jakarta.security.auth.message.callback",
    "javax.security.auth.message.config" -> "jakarta.security.auth.message.config",
    "javax.security.auth.message.module" -> "jakarta.security.auth.message.module",
    "javax.security.jacc" -> "jakarta.security.jacc",
    "javax.servlet" -> "jakarta.servlet",
    "javax.servlet.annotation" -> "jakarta.servlet.annotation",
    "javax.servlet.descriptor" -> "jakarta.servlet.descriptor",
    "javax.servlet.http" -> "jakarta.servlet.http",
    "javax.servlet.jsp" -> "jakarta.servlet.jsp",
    "javax.servlet.jsp.el" -> "jakarta.servlet.jsp.el",
    "javax.servlet.jsp.resources" -> "jakarta.servlet.jsp.resources",
    "javax.servlet.jsp.tagext" -> "jakarta.servlet.jsp.tagext",
    "javax.servlet.resources" -> "jakarta.servlet.resources",
    "javax.validation" -> "jakarta.validation",
    "javax.validation.bootstrap" -> "jakarta.validation.bootstrap",
    "javax.validation.constraints" -> "jakarta.validation.constraints",
    "javax.validation.constraintvalidation" -> "jakarta.validation.constraintvalidation",
    "javax.validation.executable" -> "jakarta.validation.executable",
    "javax.validation.groups" -> "jakarta.validation.groups",
    "javax.validation.metadata" -> "jakarta.validation.metadata",
    "javax.validation.spi" -> "jakarta.validation.spi",
    "javax.validation.valueextraction" -> "jakarta.validation.valueextraction",
    "javax.websocket" -> "jakarta.websocket",
    "javax.websocket.server" -> "jakarta.websocket.server",
    "javax.ws" -> "jakarta.ws",
    "javax.ws.rs" -> "jakarta.ws.rs",
    "javax.ws.rs.client" -> "jakarta.ws.rs.client",
    "javax.ws.rs.container" -> "jakarta.ws.rs.container",
    "javax.ws.rs.core" -> "jakarta.ws.rs.core",
    "javax.ws.rs.ext" -> "jakarta.ws.rs.ext",
    "javax.ws.rs.sse" -> "jakarta.ws.rs.sse",
    "javax.xml" -> "jakarta.xml",
    "javax.xml.namespace" -> "jakarta.xml.namespace",
    "javax.xml.registry" -> "jakarta.xml.registry",
    "javax.xml.registry.infomodel" -> "jakarta.xml.registry.infomodel",
    "javax.xml.rpc" -> "jakarta.xml.rpc",
    "javax.xml.rpc.encoding" -> "jakarta.xml.rpc.encoding",
    "javax.xml.rpc.handler" -> "jakarta.xml.rpc.handler",
    "javax.xml.rpc.handler.soap" -> "jakarta.xml.rpc.handler.soap",
    "javax.xml.rpc.holders" -> "jakarta.xml.rpc.holders",
    "javax.xml.rpc.server" -> "jakarta.xml.rpc.server",
    "javax.xml.rpc.soap" -> "jakarta.xml.rpc.soap",
    "javax.xml.soap" -> "jakarta.xml.soap",
    "javax.xml.stream" -> "jakarta.xml.stream",
    "javax.xml.stream.events" -> "jakarta.xml.stream.events",
    "javax.xml.stream.util" -> "jakarta.xml.stream.util",
    "javax.xml.ws" -> "jakarta.xml.ws",
    "javax.xml.ws.handler" -> "jakarta.xml.ws.handler",
    "javax.xml.ws.handler.soap" -> "jakarta.xml.ws.handler.soap",
    "javax.xml.ws.http" -> "jakarta.xml.ws.http",
    "javax.xml.ws.soap" -> "jakarta.xml.ws.soap",
    "javax.xml.ws.spi" -> "jakarta.xml.ws.spi",
    "javax.xml.ws.spi.http" -> "jakarta.xml.ws.spi.http",
    "javax.xml.ws.wsaddressing" -> "jakarta.xml.ws.wsaddressing"
  )

  override def fix(implicit doc: SemanticDocument): Patch = {
    doc.tree.collect {
      case t @ Importer(packageName @ Term.Select(Term.Name("javax"), _), name) =>
        mappings.get(packageName.toString()) match {
          case Some(newPackageName) =>
            val splitPackage = newPackageName.split("\\.").toList
            Patch.replaceTree(
              t,
              Importer(
                // String#split は必ず空ではない
                splitPackage.tail.foldLeft[Term.Ref](Term.Name(splitPackage.head)) { (term, name) =>
                  Term.Select(term, Term.Name(name))
                },
                name
              ).toString()
            )
          case _ => Patch.empty
        }
    }.asPatch
  }
}
