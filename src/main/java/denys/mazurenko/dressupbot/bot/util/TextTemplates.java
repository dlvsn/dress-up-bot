package denys.mazurenko.dressupbot.bot.util;

public class TextTemplates {
    public static final String START = "Привіт, %s!";
    public static final String ADD_CATEGORY_OR_AD = "Якщо хочеш додати категорію, "
            + "скористайся командою /category\n"
            + "Якщо хочеш додати оголошення, скористайся командою /ad";

    public static final String HELP_ADMIN = """
            Створити замовлення /create
            Видалити замовлення /delete {id-замовлення}
            Переглянути замовлення /find {id-замовлення}
            """;

    public static final String HELP_USER = """
            Знайти замовлення по категорії. /categories
            Показати всі замовлення /find
            """;

    public static final String AD = """
            Зоображення: %s
            Id: %d
            Назва товару: %s
            Опис: %s
            Ціна: %.2f грівєнь
            """;
}
