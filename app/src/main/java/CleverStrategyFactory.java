public class CleverStrategyFactory {
    private static CleverStrategyFactory instance = null;
    private CleverStrategyFactory() {}

    public static CleverStrategyFactory getInstance() {
        if (instance == null){
            instance = new CleverStrategyFactory();
        }
        return instance;
    }

    public CleverStrategy createStrategy (boolean isEarly) {
        if (isEarly) {
            return new EarlyStrategy();
        } else {
            return new LateStrategy();
        }
    }
}
