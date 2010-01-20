include Java

include_class 'uk.me.gumbley.minimiser.plugin.ApplicationPlugin'
include_class 'uk.me.gumbley.minimiser.plugin.AbstractPlugin'

class AppPlugin < AbstractPlugin
  def initialize() 
    @name = 'Application'
    @version = '1.0.0'
    @schema_version = '1.0'
  end
  
  attr_reader :name, :version, :schema_version
  
  def getName
    @name    
  end
  
  def getVersion
    @version    
  end
  
  def getSchemaVersion
    @schema_version    
  end
end
AppPlugin.new
