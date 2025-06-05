package denys.mazurenko.dressupbot.bot.util;

public class TextTemplates {
    public static final String START = "Привіт, %s!";
    public static final String HELP_ADMIN = """
            Створити замовлення /create
            Видалити замовлення /delete {id-замовлення}
            Переглянути замовлення /find {id-замовлення}
            """;
    public static final String AD = """
            Зоображення: %s
            Id: %d
            Назва товару: %s
            Опис: %s
            Ціна: %.2f грівєнь
            """;
}
