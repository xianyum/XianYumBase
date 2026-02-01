<template>
  <view class="verify-wrap" v-if="verifyShow">
    <view class="verify-code">
      <!-- SLIDER  ROTATE  CONCAT 类型 -->
      <block v-if="type !== 'WORD_IMAGE_CLICK'">
        <view class="verify-tip">拖动下方滑块完成拼图</view>
        <view class="verify-content">
          <view class="verify-body">
            <view class="verify-bg">
              <image
                id="bg"
                :src="captchaData.background_image"
                mode="heightFix"
              >
              </image>
            </view>
            <view
              v-if="type === 'CONCAT'"
              id="verify-concat-bg"
              class="verify-concat-bg"
              :style="imgStyle"
            >
            </view>
            <view v-else class="verify-slider" :style="imgStyle">
              <image
                id="slider-img"
                :src="captchaData.slider_image"
                mode="heightFix"
              ></image>
            </view>
            <view v-if="isSuccess" class="check-status check-success">
              <text>验证成功</text>
            </view>
            <view v-if="isErr" class="check-status check-error">
              <text>{{ errorText }}</text>
            </view>
          </view>
          <view
            @touchstart="touchstart"
            @touchmove="touchmove"
            @touchend="touchend"
            v-if="isActive"
            class="move-area"
          >
            <movable-area class="move-block" :animation="true">
              <view
                class="color-change"
                :style="{ width: colorWidth + 'px' }"
              ></view>
              <view class="move-shadow"></view>
              <movable-view
                class="block-button"
                :x="x"
                :animation="true"
                direction="horizontal"
                @change="StartMove"
              >
                <text style="font-size: 50rpx"> → </text>
              </movable-view>
            </movable-area>
          </view>
        </view>
      </block>

      <!-- 点选 -->
      <view v-else class="verify-content">
        <view class="image-click-tips">
          <text>请依次点击:</text>
          <image :src="captchaData.slider_image" mode="scaleToFill" />
        </view>
        <view class="verify-body">
          <view class="verify-bg">
            <image id="bg" :src="captchaData.background_image" mode="heightFix">
            </image>
          </view>
          <!-- 点击蒙层 -->
          <view
            id="image-click-mask"
            class="image-click-mask"
            @click="recordClickItem"
          >
            <view
              v-for="(item, index) in captchaData.trackArr"
              :key="index"
              class="click-item"
              :style="{ left: `${item.x - 15}px`, top: `${item.y - 15}px` }"
              >{{ index + 1 }}</view
            >
          </view>
        </view>
      </view>

      <!-- 刷新，关闭 操作区 -->
      <view class="verify-opts">
        <image
          class="opts-icon"
          @click="refresh"
          :src="refreshIcon"
          mode="aspectFill"
        />
        <view class="divide"></view>
        <image
          class="opts-icon"
          @click="verifyShow = false"
          :src="closeIcon"
          mode="aspectFill"
        />
      </view>
    </view>
  </view>
</template>
<script>
export default {
  name: "verify-code",
  props: {
    type: {
      type: String,
      default: "SLIDER",
      validator: (val) => {
        return (
          ["SLIDER", "CONCAT", "ROTATE", "WORD_IMAGE_CLICK"].indexOf(val) !== -1
        );
      }
    },
    conf: {
      type: Object,
      defalut: () => ({
        gen: "",
        validate: ""
      })
    }
  },
  data() {
    return {
      refreshIcon:
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAMAAAC6V+0/AAAAJFBMVEVHcEyfn59xcXFgYGBbW1tYWFhYWFhXV1dYWFhXV1dXV1dXV1eOJtjUAAAAC3RSTlMAAgkQMUd0iKbR8IVomssAAABwSURBVHjapdFBDsMgDAXR7ya2MXP/+1ZNvUCwzNsxEkjY+vNRc9Zwk5p50aarBVTc13VHQUohKSGs7wRkIDm42nMCWRFaDECbADjbGT8PvTR/cosAqTMOLWKWCfZvukTuA5GU2+hCzYtWy0vW6+j0BSLdBQYxmJeMAAAAAElFTkSuQmCC",
      closeIcon:
        "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABQAAAAUCAMAAAC6V+0/AAAAHlBMVEVHcExiYmJeXl5YWFhYWFhZWVlYWFhYWFhXV1dXV1dh3LwmAAAACXRSTlMADRxPbYyl1/NFQhX5AAAAd0lEQVR42m2R0QoDMQgEx+hl3f//4dLmeqXgPIQwIOrKIbe6tZMfKd/o0ZetWhGrZF9f18VN9bHpTh6ynYBcQHA/ZUFawKUFSxcgJ9sFIWstWQHljbyAt5D1+Vq0g2OPI9yjHMvHRuNI4/DzmnMgc3RzyBD/53gBSd8FOjnClmAAAAAASUVORK5CYII=",
      verifyShow: false,
      isActive: true, //刷新滑块
      colorWidth: uni.upx2px(80),
      leftDefault: 0,
      leftDistance: 0, //默认卡片位置固定为靠左
      isSuccess: false, //验证成功
      isErr: false, //验证失败
      errorText: "验证失败，请重新尝试！",
      x: 0, //滑块的X距离
      xpos: 0, //读取X滑动的距离
      bgImg: {
        //当前环境滑块背景尺寸
        width: 0,
        height: 0
      },
      sliderImg: {
        //当前环境滑块尺寸
        width: 0,
        height: 0
      },
      captchaData: {
        id: null, //当前生成的滑块ID, 后端生成
        background_image: "", //滑块背景图
        slider_image: "", //滑块图片
        startTime: new Date(), //起始时间
        trackArr: [], //滑动轨迹
        movePercent: 0, //滑动距离与背景图百分比
        background_image_width: 0, //当前环境滑块背景宽
        background_image_height: 0, //当前环境滑块背景高
        slider_image_width: 0, //当前环境滑块宽
        slider_image_height: 0, //当前环境滑块高
        end: 206 //滑块滑动界限值
      },
      timeout: null,
      // 点击序号
      clickCount: 0
    };
  },
  methods: {
    open() {
      this.debounce(this.getVerifyData, 80);
      this.verifyShow = true;
    },
    close() {
      this.verifyShow = false;
      this.$nextTick(() => {
        this.captchaData = {
          id: null, //当前生成的滑块ID, 后端生成
          background_image: "", //滑块背景图
          slider_image: "", //滑块图片
          startTime: new Date(), //起始时间
          trackArr: [], //滑动轨迹
          movePercent: 0, //滑动距离与背景图百分比
          background_image_width: 0, //当前环境滑块背景宽
          background_image_height: 0, //当前环境滑块背景高
          slider_image_width: 0, //当前环境滑块宽
          slider_image_height: 0, //当前环境滑块高
          end: 206 //滑块滑动界限值
        };
        this.isActive = false;
        this.$nextTick(() => {
          this.isActive = true;
        });
        this.colorWidth = 0;
        this.x = this.xpos;
        this.$nextTick(function () {
          this.x = 0;
          this.colorWidth = uni.upx2px(80);
        });
        this.isErr = false;
        this.isSuccess = false;
        this.leftDistance = 0;
      });
    },
    recordClickItem(e) {
      const x = e.touches[0].pageX;
      const y = e.touches[0].pageY;
      const relativeX = this.sliderImg.left - x;
      const relativeY = this.sliderImg.top - y;
      this.clickCount++;

      const startTime = this.captchaData.startTime;

      if (this.clickCount > 4) {
        return;
      }

      this.captchaData.stopTime = new Date();

      const track = {
        x: Math.abs(relativeX),
        y: Math.abs(relativeY),
        type: "click",
        t: new Date().getTime() - startTime.getTime()
      };

      this.captchaData.trackArr.push(track);

      if (this.clickCount == 4) {
        this.setVertifyData();
      }
    },

    // 滑块初始位置
    touchstart(e) {
      let startX = e.changedTouches[0].pageX;
      let startY = e.changedTouches[0].pageY;
      this.captchaData.startX = startX;
      this.captchaData.startY = startY;
      const pageX = this.captchaData.startX;
      const pageY = this.captchaData.startY;
      const startTime = this.captchaData.startTime;
      const trackArr = this.captchaData.trackArr;
      trackArr.push({
        x: pageX - startX,
        y: pageY - startY,
        type: "down",
        t: new Date().getTime() - startTime.getTime()
      });
    },
    // 滑块滑动中位置
    touchmove(e) {
      let pageX = Math.round(e.changedTouches[0].pageX);
      let pageY = Math.round(e.changedTouches[0].pageY);
      const startX = this.captchaData.startX;
      const startY = this.captchaData.startY;
      const startTime = this.captchaData.startTime;
      const end = this.captchaData.end;
      const bgImageWidth = this.captchaData.background_image_width;
      const trackArr = this.captchaData.trackArr;
      let moveX = pageX - startX;
      const track = {
        x: pageX - startX,
        y: pageY - startY,
        type: "move",
        t: new Date().getTime() - startTime.getTime()
      };
      trackArr.push(track);
      if (moveX < 0) {
        moveX = 0;
      } else if (moveX > end) {
        moveX = end;
      }
      this.captchaData.moveX = moveX;
      this.captchaData.movePercent = moveX / bgImageWidth;
    },
    // 滑块停止滑动
    touchend(e) {
      this.captchaData.stopTime = new Date();
      let pageX = Math.round(e.changedTouches[0].pageX);
      let pageY = Math.round(e.changedTouches[0].pageY);
      const startX = this.captchaData.startX;
      const startY = this.captchaData.startY;
      const startTime = this.captchaData.startTime;
      const trackArr = this.captchaData.trackArr;
      const track = {
        x: pageX - startX,
        y: pageY - startY,
        type: "up",
        t: new Date().getTime() - startTime.getTime()
      };
      trackArr.push(track);
      this.setVertifyData();
    },
    // 滑块绑定卡片移动距离
    StartMove(e) {
      this.xpos = e.detail.x;
      // console.log("xpos", this.xpos)
      this.$nextTick(() => {
        this.colorWidth = this.xpos + uni.upx2px(80);
        this.leftDistance = this.xpos;
      });
    },
    // 获取背景图和滑块图片
    getVerifyData() {
      this.$modal.loading('正在加载验证码')
      this.clickCount = 0;
      this.captchaData.trackArr = [];
      this.captchaData.startTime = new Date();
      uni.request({
        url: `${this.conf.gen}?type=${this.type}`,
        method: "post",
        success: ({ data }) => {
          this.captchaData.id = data.data.id;
          this.captchaData.background_image = data.data.backgroundImage;
          this.captchaData.slider_image = data.data.templateImage;
          this.$modal.closeLoading()
          this.$nextTick(() => {
            // 获取当前设备上渲染的背景图和滑块尺寸
            let query = uni.createSelectorQuery().in(this);
            query
              .select("#bg")
              .boundingClientRect((res) => {
                this.bgImg.width = res.width;
                this.bgImg.height = res.height;
              })
              .exec();

            if (this.type === "ROTATE" || this.type === "SLIDER") {
              query
                .select("#slider-img")
                .boundingClientRect((res) => {
                  this.sliderImg.width = res.width;
                  this.sliderImg.height = res.height;
                })
                .exec();
            } else if (this.type === "CONCAT") {
              query
                .select("#verify-concat-bg")
                .boundingClientRect((res) => {
                  const height =
                    ((data.captcha.backgroundImageHeight -
                      data.captcha.data.randomY) /
                      data.captcha.backgroundImageHeight) *
                    uni.upx2px(data.captcha.backgroundImageHeight);
                  this.sliderImg.height = height;
                })
                .exec();
            } else if (this.type === "WORD_IMAGE_CLICK") {
              query
                .select("#image-click-mask")
                .boundingClientRect((res) => {
                  this.sliderImg.left = res.left;
                  this.sliderImg.top = res.top;
                })
                .exec();
            }
            setTimeout(() => {
              this.captchaData.end = this.bgImg.width - uni.upx2px(40);
              this.initConfig(
                this.bgImg.width,
                this.bgImg.height,
                this.sliderImg.width,
                this.sliderImg.height,
                this.captchaData.end
              );
            }, 1000);
          });
        }
      });
    },
    // 初始化配置
    initConfig(
      bgImageWidth,
      bgImageHeight,
      sliderImageWidth,
      sliderImageHeight,
      end
    ) {
      this.captchaData.background_image_width = Math.round(bgImageWidth);
      this.captchaData.background_image_height = Math.round(bgImageHeight);
      this.captchaData.slider_image_width = Math.round(sliderImageWidth);
      this.captchaData.slider_image_height = Math.round(sliderImageHeight);
      this.captchaData.end = Math.round(end);
    },
    // 校验是否成功
    setVertifyData() {
      const dataForm = {
        id: this.captchaData.id,
        data: {
          bgImageWidth: this.captchaData.background_image_width,
          bgImageHeight: this.captchaData.background_image_height,
          startTime: new Date(this.captchaData.startTime).getTime(),
          stopTime: new Date(this.captchaData.stopTime).getTime(),
          trackList: this.captchaData.trackArr
        }
      };

      uni.request({
        url: `${this.conf.validate}?type=${this.type}`,
        method: "post",
        data: dataForm,
        success: (res) => {
          if (res.data.code == "200") {
            if (res.data.data) {
              this.isSuccess = true;
              // uni.showToast({
              //   title: "验证成功",
              //   icon: "none"
              // });
              let tm = setTimeout(() => {
                clearTimeout(tm);
                this.close();
                this.$emit("success", res.data.data.id);
              }, 1000);
            }
          } else {
            uni.showToast({
              title: "验证失败",
              icon: "none"
            });
            let tm = setTimeout(() => {
              clearTimeout(tm);
              this.refresh();
            }, 300);
          }
        },
        fail: (err) => {
          this.refresh();
        }
      });
    },
    // 刷新滑块
    refresh() {
      this.isActive = false;

      this.$nextTick(() => {
        this.isActive = true;
      });
      this.colorWidth = 0;
      this.x = this.xpos;
      this.$nextTick(function () {
        this.x = 0;
        this.colorWidth = uni.upx2px(80);
      });

      this.isErr = false;
      this.isSuccess = false;
      this.leftDistance = 0;
      this.clickCount = 0;
      this.captchaData.trackArr = [];
      this.getVerifyData();
    },
    debounce(func, wait = 500, immediate = false) {
      // 清除定时器
      if (this.timeout !== null) clearTimeout(this.timeout);
      // 立即执行，此类情况一般用不到
      if (immediate) {
        const callNow = !this.timeout;
        this.timeout = setTimeout(() => {
          this.timeout = null;
        }, wait);
        if (callNow) typeof func === "function" && func();
      } else {
        this.timeout = setTimeout(() => {
          typeof func === "function" && func();
        }, wait);
      }
    }
  },
  computed: {
    imgStyle() {
      if (this.type === "ROTATE") {
        const angle = this.leftDistance / (this.captchaData.end / 360);
        return `transform:translate(100%,0) rotate(${angle - 5}deg);`;
      }
      if (this.type === "SLIDER") {
        return `left: ${this.leftDistance}px;`;
      }

      if (this.type === "CONCAT") {
        return `background-position:${this.leftDistance}px 0;background-image:url(${this.captchaData.background_image});background-size: cover;height:${this.sliderImg.height}px`;
      }
    }
  }
};
</script>

<style scoped lang="scss">
.verify-wrap {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  overflow: hidden;

  .verify-code {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    width: 640rpx;
    max-height: 740rpx;
    background-color: #ffffff;
    padding: 40rpx 0 20rpx;
    z-index: 999;
    box-shadow: 0 0 10rpx rgba(227, 227, 227, 0.7);
    border-radius: 10px;
    overflow: hidden;

    .verify-tip {
      font-size: 32rpx;
      font-weight: bold;
      color: #686868;
      padding: 0 20rpx;
    }

    .verify-content {
      width: 100%;
      padding: 20rpx 20rpx;
      background-color: #ffffff;
      box-sizing: border-box;
      overflow: hidden;

      .verify-concat-bg {
        width: 100%;
        position: absolute;
        top: 0;
        left: 0;
      }

      .image-click-tips {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 40rpx;

        image {
          width: 420rpx;
          height: 80rpx;
        }
      }

      .image-click-mask {
        width: 100%;
        height: 100%;
        position: absolute;
        top: 0;
        left: 0;
        z-index: 999;

        .click-item {
          position: absolute;
          left: 0;
          top: 0;
          z-index: 1000;
          border-radius: 50px;
          background-color: #409eff;
          width: 50rpx;
          height: 50rpx;
          text-align: center;
          line-height: 50rpx;
          color: #fff;
          border: 4rpx solid #fff;
          box-sizing: content-box;
        }
      }

      .verify-body {
        width: 100%;
        height: 360rpx;
        border-radius: 6px;
        position: relative;
        overflow: hidden;

        .verify-bg {
          width: 100%;
          height: 100%;
          position: absolute;

          image {
            width: 100%;
            height: 100%;
          }
        }

        .verify-slider {
          height: 100%;
          position: absolute;
          left: 0;
          top: 0;

          image {
            overflow: hidden;
            width: 55px;
            height: 100%;
            position: relative;
          }
        }
      }

      .move-area {
        overflow: hidden;
        width: 100%;
        height: 80rpx;
        margin-top: 20rpx;
      }

      .move-block {
        width: 100%;
        height: 100%;
        background-color: #f0f0f0;
        border-radius: 100rpx;
        position: relative;
        overflow: hidden;

        .move-shadow {
          height: 100%;
          width: 4px;
          background-color: rgba(255, 255, 255, 0.5);
          position: absolute;
          top: 0;
          left: 0;
          box-shadow: 1px 1px 1px #fff;
          border-radius: 50%;
          animation: moveAnimate 2s linear infinite;
        }

        @keyframes moveAnimate {
          0% {
            left: 0;
            opacity: 0.5;
          }

          50% {
            left: 50%;
            opacity: 1;
          }

          100% {
            left: 100%;
            opacity: 0.5;
          }
        }

        .color-change {
          height: 80rpx;
          border-radius: 100rpx;
          background-color: #c6a876;
          z-index: 2;
        }

        .block-button {
          border-radius: 100rpx;
          background-color: #b48d4d;
          height: 80rpx;
          width: 80rpx;
          margin-top: -10rpx;
          touch-action: none;
          display: flex;
          flex-direction: row;
          align-items: center;
          justify-content: center;
          color: #fff;
        }
      }

      .check-status {
        position: absolute;
        left: 0;
        right: 0;
        bottom: -1px;
        height: 50rpx;
        line-height: 50rpx;
        width: 100%;
        text-align: center;
        font-size: 24rpx;
        color: #fff;

        &.check-success {
          background: #5ac725;
        }

        &.check-error {
          background: #f56c6c;
        }
      }
    }

    .verify-opts {
      display: flex;
      justify-content: flex-end;
      align-items: center;
      margin: 0 20rpx;

      .opts-icon {
        width: 40rpx;
        height: 40rpx;

        &:nth-last-child(1) {
          width: 50rpx;
          height: 50rpx;
        }
      }

      .divide {
        height: 20px;
        width: 40rpx;
      }
    }
  }
}
</style>
