package transformer.model.database

import transformer.model.units.UserRow

object SeedData {
  val initialUses: Seq[UserRow] = Seq(
    UserRow(None, "Витя", 40, "УАЗ", "Улгу, Политех", currentStatusActive = true),
    UserRow(None, "Артем", 22, "Ультра", "УлГПУ, Улет", currentStatusActive = true),
    UserRow(None, "Алексей", 23, "Автозавод", "КЭИ, Яндекс-доставка", currentStatusActive = false),
    UserRow(None, "Никита", 21, "УИ ГА", "УИ ГА, Озон", currentStatusActive = true),
    UserRow(None, "Елена", 24, "Тинькофф", "УлГТУ, Альфа-Банк", currentStatusActive = true),
    UserRow(None, "Сергей", 25, "Ростелеком", "УлГУ, Билайн", currentStatusActive = false)
  )
}
