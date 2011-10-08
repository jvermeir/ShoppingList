package shop

object ShoppingList {

  def main(args: Array[String]): Unit = {
    val recepten = """naam:Lasagne met gehakt
vlees:gehakt
saus:gezeefde tomaten
saus:lasagnebladen
diepvries:spinazie
zuivel:geraspte kaas

naam:Wafels met soep en pastasalade
meel:zelfrijzend bakmeel
zuivel:ei (5)
zuivel:melk
meel:soep
groente:sla
groente:tomaat
pasta:fusilli
groente:aubergine
groente:paprika
groente:courgette
pasta:pesto
kruiden:basilicum

naam:Nasi
groente:nasi pakket
vlees:kipfilet
saus:sate saus
pasta:rijst
pasta:kroepoek
meel:augurken
meel:zilveruitjes
zuivel:ei (5)
zuivel:vloeibare bakboter

naam:Roti
saus:roti pannenkoeken
vlees:kip
blikken:sperziebonen
groente:krieltjes

naam:Preitaart
groente:prei
groente:selderij
groente:wortel
vlees:kleine braadworstjes
vlees:kip
diepvries:bladerdeeg

naam:Spaghetti met zalmpakketje
pasta:spaghetti
pasta:pesto
vis:zalm
groente:prei
groente:tomaat
folie:aluminiumfolie

naam:varkenshaas met verse pasta
vlees:varkenshaas
verse pasta:pasta
groente:champignons
saus:roomsaus
"""
    val menuText = """Zaterdag:Lasagne met gehakt
Zondag:Wafels met soep en pastasalade
Maandag:Nasi
Dinsdag:Roti
Woensdag:Preitaart
Donderdag:Spaghetti met zalmpakketje
Vrijdag:varkenshaas met verse pasta
"""
    val menu = Menu(menuText, CookBook(recepten))
    val boodschappenlijst = menu.printShoppinglist
    println(boodschappenlijst)
  }

}