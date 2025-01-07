# How to get the plugin's API reference

The API provides the `Teams` interface (which plugin implements) to access to the API classes, for get it, you must use the `TeamsProvider#get` class's function to obtaing the reference.

You must notice that if you try to get the API reference when plugin isn't fully enabled yet, the function will throw an `IllegalStateException` error, as well for any of the
interface's methods, to avoid this, you must call to this function preferably during your plugin's `onEnable` method.

Once you have the API instance, you can access to all the interface's methods, you can see all the available interface's methods, as well
their documentation to understand what they do, and which you could need.

A code example getting the API instance and accessing to a class's reference.
```java
public final class TestPlugin extends JavaPlugin {
  private TagAggregateRootRegistry tagAggregateRootRegistry;

  @Override
  public void onEnable() {
    final Teams teams = TeamsProvider.get();
    this.tagAggregateRootRegistry = teams.tagsRegistry();
  }
}
```
