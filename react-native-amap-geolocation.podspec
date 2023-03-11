require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  # s.name         = package["name"]
  s.name         = "react-native-amap-geolocation"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = "https://github.com/uiwjs/react-native-amap-geolocation"
  # brief license entry:
  s.license      = package["license"]
  s.author       = { package["author"]["name"] => package["author"]["email"] }
  # optional - use expanded license entry instead:
  # s.license    = { :type => "MIT", :file => "LICENSE" }
  # s.authors      = { "Kenny Wong" => "wowohoo@qq.com" }
  s.platforms    = { :ios => "9.0" }
  s.source       = { :git => "https://github.com/uiwjs/react-native-amap-geolocation.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm}"
  s.requires_arc = true

  s.dependency "React"
  s.dependency "AMapLocation", "~> 2.9.0"
  # ...
  # s.dependency "..."
end

