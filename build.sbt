name := "akka-cluster-k8s"
version := "1.0"
scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-cluster" % "2.5.13",
  "com.typesafe.akka" %% "akka-http" % "10.1.3",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.3" % Test
)

enablePlugins(SbtReactiveAppPlugin)

akkaClusterBootstrapSystemName := "K8sCluster"
dockerUpdateLatest := true
dockerRepository := Some("XXXX.dkr.ecr.us-east-1.amazonaws.com") //AWS Repository angeben!
packageName in Docker := "k8sakkaclustersample" //Docker Image Name
endpoints += HttpEndpoint("http", HttpIngress(Vector(80, 443), Vector.empty, Vector.empty))
enableAkkaClusterBootstrap := true
deployMinikubeAkkaClusterBootstrapContactPoints := 3
deployMinikubeRpArguments ++= Vector(
  "--ingress-annotation", "ingress.kubernetes.io/rewrite-target=/",
  "--ingress-annotation", "nginx.ingress.kubernetes.io/rewrite-target=/"
)